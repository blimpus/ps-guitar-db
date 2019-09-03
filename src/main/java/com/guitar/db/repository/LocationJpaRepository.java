package com.guitar.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.guitar.db.model.Location;

@Repository
public interface LocationJpaRepository extends JpaRepository<Location, Long> {

	List<Location> findByStateLike(String stateName);
	
	List<Location> findByStateOrCountry(String value, String value2);
	
	List<Location> findByStateAndCountry(String state, String country);
	
	List<Location> findByStateIsOrCountryEquals(String value, String value2);
	
	List<Location> findByStateNot(String state);
	
	List<Location> findByStateNotLike(String stateName);

	List<Location> findByStateStartingWith(String stateName);

	List<Location> findByStateEndingWith(String stateName);
	
	List<Location> findByStateContaining(String stateName);
	
	List<Location> findByStateIgnoreCaseStartingWith(String statename);
	
	List<Location> findByStateNotLikeOrderByStateAsc(String stateName);
	
	
}
