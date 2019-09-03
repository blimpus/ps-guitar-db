package com.guitar.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotSame;
import static org.hamcrest.CoreMatchers.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.guitar.db.model.Location;
import com.guitar.db.repository.LocationJpaRepository;

@ContextConfiguration(locations={"classpath:com/guitar/db/applicationTests-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class LocationPersistenceTests {
	
	@Autowired
	private LocationJpaRepository locationJpaRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Test
	@Transactional
	public void testSaveAndGetAndDelete() throws Exception {
		Location location = new Location();
		location.setCountry("Canada");
		location.setState("British Columbia");
		location = locationJpaRepository.saveAndFlush(location);
		
		// clear the persistence context so we don't return the previously cached location object
		// this is a test only thing and normally doesn't need to be done in prod code
		entityManager.clear();

		Location otherLocation = locationJpaRepository.findOne(location.getId());
		assertEquals("Canada", otherLocation.getCountry());
		assertEquals("British Columbia", otherLocation.getState());
		
		//delete BC location now
		locationJpaRepository.delete(otherLocation);
	}

	@Test
	public void testFindWithLike() throws Exception {
		List<Location> locs = locationJpaRepository.findByStateLike("New%");
		assertEquals(4, locs.size());
	}

	@Test
	@Transactional  //note this is needed because we will get a lazy load exception unless we are in a tx
	public void testFindWithChildren() throws Exception {
		Location arizona = locationJpaRepository.findOne(3L);
		assertEquals("United States", arizona.getCountry());
		assertEquals("Arizona", arizona.getState());
		
		assertEquals(1, arizona.getManufacturers().size());
		
		assertEquals("Fender Musical Instruments Corporation", arizona.getManufacturers().get(0).getName());
	}
	
	@Test
	public void testFindAll() {
		List<Location> ls = locationJpaRepository.findAll();
		assertNotNull(ls);
		
	}
	
	@Test
	public void testJpaAnd() {
		List<Location> ls = locationJpaRepository.findByStateAndCountry("Utah","United States");
		assertNotNull(ls);
		
		assertEquals("Utah",ls.get(0).getState());
	}
	
	@Test
	public void testJpaOr() {
		List<Location> ls = locationJpaRepository.findByStateOrCountry("Utah", "Utah");
		assertNotNull(ls);
		
		assertEquals("Utah",ls.get(0).getState());
	}
	
	@Test
	public void testJpaisEquals() {
		List<Location> ls = locationJpaRepository.findByStateIsOrCountryEquals("Utah", "Utah");
		assertNotNull(ls);
		
		assertEquals("Utah",ls.get(0).getState());
		
	}
	
	@Test
	public void testJpaNot() {
		List<Location> ls = locationJpaRepository.findByStateNot("Utah");
		assertNotNull(ls);
		
		assertThat("Utah",not(ls.get(0).getState()));
		//another way to assert
		assertNotSame("Utah",ls.get(0).getState());
	}
	
	@Test
	public void testFindWithNotLike() throws Exception {
		List<Location> locs = locationJpaRepository.findByStateNotLike("New%");
		
		assertEquals(46, locs.size());
	}
	
	@Test
	public void testFindWithStartingWith() throws Exception {
		List<Location> locs = locationJpaRepository.findByStateStartingWith("Cali");
		
		assertEquals("California",locs.get(0).getState());
	}
	
	@Test
	public void testFindWithEndingWith() throws Exception {
		List<Location> locs = locationJpaRepository.findByStateEndingWith("ico");
		
		assertEquals("New Mexico",locs.get(0).getState());
	}
	
	@Test
	public void testFindWithContaining() throws Exception {
		List<Location> locs = locationJpaRepository.findByStateContaining("abam");
		
		assertEquals("Alabama",locs.get(0).getState());;
	}
	
	@Test
	public void testFindWithStateIgnoreCase() throws Exception {
		List<Location> locs = locationJpaRepository.findByStateIgnoreCaseStartingWith("ca");
		
		assertTrue(locs.get(0).getState().equals("California"));
	}
	
	@Test
	public void testFindWithNotLikeSortingByStateAsc() throws Exception {
		List<Location> locs = locationJpaRepository.findByStateNotLikeOrderByStateAsc("New");
		
		locs.forEach((location) -> {
			System.out.println(locs.get(0).getState());
		});
		//assertEquals(46, locs.size());
	}
}
