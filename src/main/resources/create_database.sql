-- CLEAR TABLES
DROP TABLE IF EXISTS "applicant" CASCADE;
DROP TABLE IF EXISTS "applied_instrument" CASCADE;
DROP TABLE IF EXISTS "enrollment_application" CASCADE;
DROP TABLE IF EXISTS "ensemble_participant" CASCADE;
DROP TABLE IF EXISTS "ensemble" CASCADE;
DROP TABLE IF EXISTS "ensemble_pricing_scheme" CASCADE;
DROP TABLE IF EXISTS "ensemble_time" CASCADE;
DROP TABLE IF EXISTS "group_lesson" CASCADE;
DROP TABLE IF EXISTS "group_lesson_participant" CASCADE;
DROP TABLE IF EXISTS "group_lesson_time" CASCADE;
DROP TABLE IF EXISTS "individual_lesson" CASCADE;
DROP TABLE IF EXISTS "instructor" CASCADE;
DROP TABLE IF EXISTS "taught_instrument" CASCADE;
DROP TABLE IF EXISTS "instructor_time_slot" CASCADE;
DROP TABLE IF EXISTS "instrument_rental" CASCADE;
DROP TABLE IF EXISTS "instrument_played_in_ensemble" CASCADE;
DROP TABLE IF EXISTS "lesson_pricing_scheme" CASCADE;
DROP TABLE IF EXISTS "musical_instrument" CASCADE;
DROP TABLE IF EXISTS "musical_instrument_to_rent" CASCADE;
DROP TABLE IF EXISTS "parent" CASCADE;
DROP TABLE IF EXISTS "parent_detail" CASCADE;
DROP TABLE IF EXISTS "parent_email" CASCADE;
DROP TABLE IF EXISTS "parent_phone" CASCADE;
DROP TABLE IF EXISTS "person" CASCADE;
DROP TABLE IF EXISTS "person_email" CASCADE;
DROP TABLE IF EXISTS "person_phone" CASCADE;
DROP TABLE IF EXISTS "school" CASCADE;
DROP TABLE IF EXISTS "student" CASCADE;
DROP TABLE IF EXISTS "student_sibling" CASCADE;

-- CLEAR VIEWS
DROP VIEW IF EXISTS "monthly_lesson_statistic" CASCADE;
DROP MATERIALIZED VIEW IF EXISTS "generated_month" CASCADE;
DROP MATERIALIZED VIEW IF EXISTS "instructor_lesson" CASCADE;
DROP VIEW IF EXISTS "next_week_ensemble_booking_status" CASCADE;
DROP VIEW IF EXISTS "rental_with_instrument" CASCADE;


-- DROP FUNCTIONS
DROP FUNCTION IF EXISTS check_for_rental_quota CASCADE;

-- TABLES

CREATE TABLE "applicant" (
    "id" serial PRIMARY KEY NOT NULL,
    "person_id" integer NOT NULL
);

CREATE TABLE "applied_instrument" (
    "application_id" integer NOT NULL,
    "instrument_id" integer NOT NULL,
    "skillfulness_level" character varying(50) NOT NULL,
    PRIMARY KEY ("application_id", "instrument_id")
);

CREATE TABLE "enrollment_application" (
    "id" serial PRIMARY KEY NOT NULL,
    "application_id" character varying(50) UNIQUE NOT NULL,
    "submitting_date" date NOT NULL,
    "applicant_id" integer NOT NULL,
    "applied_ensemble_id" integer,
    "place_offered" BOOLEAN,
    "place_accepted" BOOLEAN
);

CREATE TABLE "ensemble_participant" (
    "student_id" integer NOT NULL,
    "ensemble_id" integer NOT NULL,
    PRIMARY KEY ("student_id", "ensemble_id")
);


CREATE TABLE "ensemble" (
    "id" serial PRIMARY KEY NOT NULL,
    "ensemble_id" character varying(50) UNIQUE NOT NULL,
    "target_genre" character varying(50) NOT NULL,
    "min_number_of_seats" integer NOT NULL,
    "max_number_of_seats" integer NOT NULL,
    "instructor_id" integer NOT NULL
);

CREATE TABLE "ensemble_time" (
    "ensemble_id" integer NOT NULL,
    "date" DATE NOT NULL,
    "start_time" time NOT NULL,
    "end_time" time NOT NULL,
    PRIMARY KEY ("ensemble_id", "date", "start_time", "end_time")
);

CREATE TABLE "ensemble_pricing_scheme" (
    "id" serial PRIMARY KEY NOT NULL,
    "price" double precision NOT NULL,
    "sibling_discount" double precision NOT NULL,
    "valid_from" date NOT NULL
);

CREATE TABLE "group_lesson" (
    "id" serial PRIMARY KEY NOT NULL,
    "lesson_id" character varying(50) UNIQUE NOT NULL,
    "difficulty_level" character varying(50) NOT NULL,
    "min_number_of_seats" integer NOT NULL,
    "max_number_of_seats" integer NOT NULL,
    "instructor_id" integer NOT NULL,
    "taught_instrument_id" integer NOT NULL
);

CREATE TABLE "group_lesson_time" (
    "group_lesson_id" integer NOT NULL,
    "date" DATE NOT NULL,
    "start_time" time NOT NULL,
    "end_time" time NOT NULL,
    PRIMARY KEY ("group_lesson_id", "date", "start_time", "end_time")
);

CREATE TABLE "group_lesson_participant"(
	"student_id" integer NOT NULL,
	"group_lesson_id" integer NOT NULL,
	PRIMARY KEY ("student_id", "group_lesson_id")
);	

CREATE TABLE "individual_lesson" (
    "id" serial PRIMARY KEY NOT NULL,
    "difficulty_level" character varying(50) NOT NULL,
    "instructor_id" integer NOT NULL,
    "student_id" integer NOT NULL,
    "taught_instrument_id" integer NOT NULL,
    "booked_time_slot_id" integer NOT NULL
);

CREATE TABLE "instructor" (
    "id" serial PRIMARY KEY NOT NULL,
    "employment_id" character varying(50) UNIQUE NOT NULL,
    "person_id" integer NOT NULL
);

CREATE TABLE "taught_instrument" (
    "instructor_id" integer NOT NULL,
    "instrument_id" integer NOT NULL,
    PRIMARY KEY ("instructor_id", "instrument_id")
);

CREATE TABLE "instructor_time_slot" (
    "id" serial PRIMARY KEY NOT NULL,
    "instructor_id" integer NOT NULL,
    "date" DATE NOT NULL,
    "start_time" time NOT NULL,
    "end_time" time NOT NULL,
    "is_booked" BOOLEAN NOT NULL
);

CREATE TABLE "instrument_rental" (
    "id" serial PRIMARY KEY NOT NULL,
    "rental_id" character(50) UNIQUE NOT NULL,
    "start_date" date NOT NULL,
    "end_date" date NOT NULL,
    "student_id" integer NOT NULL,
    "instrument_id" integer NOT NULL
);

CREATE TABLE "instrument_played_in_ensemble" (
    "instrument_id" integer NOT NULL,
    "ensemble_id" integer NOT NULL,
    PRIMARY KEY ("instrument_id", "ensemble_id")
);

CREATE TABLE "lesson_pricing_scheme" (
    "id" serial PRIMARY KEY NOT NULL,
    "lesson_type" character varying(50) NOT NULL,
    "difficulty_level" character varying(50) NOT NULL,
    "price" double precision NOT NULL,
    "sibling_discount" double precision NOT NULL,
    "valid_from" date NOT NULL
);

CREATE TABLE "musical_instrument" (
    "id" serial PRIMARY KEY NOT NULL,
    "type" character varying(50) UNIQUE NOT NULL
);

CREATE TABLE "musical_instrument_to_rent" (
    "id" serial PRIMARY KEY NOT NULL,
    "instrument_id" character varying(50) UNIQUE NOT NULL,
    "brand" character varying(100) NOT NULL,
    "stock_quantity" integer CHECK (stock_quantity >= 0) NOT NULL,
    "monthly_rental_fee" double precision NOT NULL,
    "instrument_type_id" integer NOT NULL
);

CREATE TABLE "parent" (
    "id" serial PRIMARY KEY NOT NULL,
    "first_name" character varying(100) NOT NULL,
    "last_name" character varying(100) NOT NULL
);

CREATE TABLE "parent_detail" (
    "student_id" integer NOT NULL,
    "parent_id" integer NOT NULL,
    PRIMARY KEY ("student_id", "parent_id")
);

CREATE TABLE "parent_email" (
    "parent_id" integer NOT NULL,
    "parent_email" character varying(50) UNIQUE NOT NULL,
    PRIMARY KEY ("parent_id", "parent_email")
);

CREATE TABLE "parent_phone" (
    "parent_id" integer NOT NULL,
    "parent_phone" character varying(50) UNIQUE NOT NULL,
    PRIMARY KEY ("parent_id", "parent_phone")
);

CREATE TABLE "person" (
    "id" serial PRIMARY KEY NOT NULL,
    "first_name" character varying(100) NOT NULL,
    "last_name" character varying(100) NOT NULL,
    "person_number" character varying(12) UNIQUE NOT NULL,
    "date_of_birth" date NOT NULL,
    "address" character varying(500) NOT NULL
);

CREATE TABLE "person_email" (
    "person_id" integer NOT NULL,
    "person_email" character varying(50) UNIQUE NOT NULL,
    PRIMARY KEY ("person_id", "person_email")
);

CREATE TABLE "person_phone" (
    "person_phone" character varying(50) UNIQUE NOT NULL,
    "person_id" integer NOT NULL,
    PRIMARY KEY ("person_id", "person_phone")
);

CREATE TABLE "school" (
    "school_id" character varying(50) NOT NULL,
    "name" character varying(100) NOT NULL,
    "address" character varying(500) NOT NULL,
    PRIMARY KEY ("school_id")
);

CREATE TABLE "student" (
    "id" serial PRIMARY KEY NOT NULL,
    "student_id" character varying(50) UNIQUE NOT NULL,
    "person_id" integer NOT NULL
);

CREATE TABLE "student_sibling" (
    "student_id" integer NOT NULL,
    "sibling_student_id" integer NOT NULL
);

-- FOREIGN KEY CONSTRAINTS

-- FK for applicant
ALTER TABLE ONLY "applicant" ADD CONSTRAINT "applicant_person_id_fkey"
    FOREIGN KEY (person_id) REFERENCES person(id) ON DELETE CASCADE NOT DEFERRABLE;

-- FK for applied_instrument
ALTER TABLE ONLY "applied_instrument" ADD CONSTRAINT "applied_instrument_application_id_fkey"
    FOREIGN KEY (application_id) REFERENCES enrollment_application(id) ON DELETE CASCADE NOT DEFERRABLE;
ALTER TABLE ONLY "applied_instrument" ADD CONSTRAINT "applied_instrument_instrument_id_fkey"
    FOREIGN KEY (instrument_id) REFERENCES musical_instrument(id) ON DELETE CASCADE NOT DEFERRABLE;

-- FK for enrollment_application
ALTER TABLE ONLY "enrollment_application" ADD CONSTRAINT "enrollment_application_applied_ensemble_id_fkey"
    FOREIGN KEY (applied_ensemble_id) REFERENCES ensemble(id) NOT DEFERRABLE;
ALTER TABLE ONLY "enrollment_application" ADD CONSTRAINT "enrollment_application_applicant_id_fkey"
    FOREIGN KEY (applicant_id) REFERENCES applicant(id) ON DELETE CASCADE NOT DEFERRABLE;

-- FK for ensemble
ALTER TABLE ONLY "ensemble" ADD CONSTRAINT "ensemble_instructor_id_fkey"
    FOREIGN KEY (instructor_id) REFERENCES instructor(id) NOT DEFERRABLE;

-- FK for ensemble_time
ALTER TABLE ONLY "ensemble_time" ADD CONSTRAINT "ensemble_time_ensemble_id_fkey"
    FOREIGN KEY (ensemble_id) REFERENCES ensemble(id) ON DELETE CASCADE NOT DEFERRABLE;

--FK for ensemble_participants
ALTER TABLE ONLY "ensemble_participant" ADD CONSTRAINT "ensemble_participant_ensemble_id_fkey"
    FOREIGN KEY (ensemble_id) REFERENCES ensemble(id) ON DELETE CASCADE NOT DEFERRABLE;
ALTER TABLE ONLY "ensemble_participant" ADD CONSTRAINT "ensemble_participant_student_id_fkey"
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE NOT DEFERRABLE;

-- FK for group_lesson
ALTER TABLE ONLY "group_lesson" ADD CONSTRAINT "group_lesson_instructor_id_fkey"
    FOREIGN KEY (instructor_id) REFERENCES instructor(id) NOT DEFERRABLE;
ALTER TABLE ONLY "group_lesson" ADD CONSTRAINT "group_lesson_taught_instrument_id_fkey"
    FOREIGN KEY (taught_instrument_id) REFERENCES musical_instrument(id) NOT DEFERRABLE;

--FK for group_lesson_participant
ALTER TABLE ONLY "group_lesson_participant" ADD CONSTRAINT "group_lesson_participant_group_lesson_id_fkey"
    FOREIGN KEY (group_lesson_id) REFERENCES group_lesson(id) ON DELETE CASCADE NOT DEFERRABLE;
ALTER TABLE ONLY "group_lesson_participant" ADD CONSTRAINT "group_lesson_participant_student_id_fkey"
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE NOT DEFERRABLE;

-- FK for group_lesson_time
ALTER TABLE ONLY "group_lesson_time" ADD CONSTRAINT "group_lesson_time_group_lesson_id_fkey"
    FOREIGN KEY (group_lesson_id) REFERENCES group_lesson(id) ON DELETE CASCADE NOT DEFERRABLE;

-- FK for individual_lesson
ALTER TABLE ONLY "individual_lesson" ADD CONSTRAINT "individual_lesson_instructor_id_fkey"
    FOREIGN KEY (instructor_id) REFERENCES instructor(id) NOT DEFERRABLE;
ALTER TABLE ONLY "individual_lesson" ADD CONSTRAINT "individual_lesson_student_id_fkey"
    FOREIGN KEY (student_id) REFERENCES student(id) NOT DEFERRABLE;
ALTER TABLE ONLY "individual_lesson" ADD CONSTRAINT "individual_lesson_taught_instrument_id_fkey"
    FOREIGN KEY (taught_instrument_id) REFERENCES musical_instrument(id) NOT DEFERRABLE;
ALTER TABLE ONLY "individual_lesson" ADD CONSTRAINT "individual_lesson_booked_time_slot_id_fkey"
    FOREIGN KEY (booked_time_slot_id) REFERENCES instructor_time_slot(id) NOT DEFERRABLE;

-- FK for instructor
ALTER TABLE ONLY "instructor" ADD CONSTRAINT "instructor_person_id_fkey"
    FOREIGN KEY (person_id) REFERENCES person(id) NOT DEFERRABLE;

-- FK for instructor_instrument
ALTER TABLE ONLY "taught_instrument" ADD CONSTRAINT "taught_instrument_instructor_id_fkey"
    FOREIGN KEY (instructor_id) REFERENCES instructor(id) ON DELETE CASCADE NOT DEFERRABLE;
ALTER TABLE ONLY "taught_instrument" ADD CONSTRAINT "taught_instrument_instrument_id_fkey"
    FOREIGN KEY (instrument_id) REFERENCES musical_instrument(id) ON DELETE CASCADE NOT DEFERRABLE;

-- FK for instructor_time_slot
ALTER TABLE ONLY "instructor_time_slot" ADD CONSTRAINT "instructor_time_slot_instructor_id_fkey"
    FOREIGN KEY (instructor_id) REFERENCES instructor(id) ON DELETE CASCADE NOT DEFERRABLE;

-- FK for instrument_played_in_ensemble
ALTER TABLE ONLY "instrument_played_in_ensemble" ADD CONSTRAINT "instrument_played_in_ensemble_instrument_id_fkey"
    FOREIGN KEY (instrument_id) REFERENCES musical_instrument(id) ON DELETE CASCADE NOT DEFERRABLE;
ALTER TABLE ONLY "instrument_played_in_ensemble" ADD CONSTRAINT "instrument_played_in_ensemble_ensemble_id_fkey"
    FOREIGN KEY (ensemble_id) REFERENCES ensemble(id) ON DELETE CASCADE NOT DEFERRABLE;


-- FK for instrument_rental
ALTER TABLE ONLY "instrument_rental" ADD CONSTRAINT "instrument_rental_instrument_id_fkey"
    FOREIGN KEY (instrument_id) REFERENCES musical_instrument_to_rent(id) ON DELETE CASCADE NOT DEFERRABLE;
ALTER TABLE ONLY "instrument_rental" ADD CONSTRAINT "instrument_rental_student_id_fkey"
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE NOT DEFERRABLE;

-- FK for musical_instrument_to_rent
ALTER TABLE ONLY "musical_instrument_to_rent" ADD CONSTRAINT "musical_instrument_to_rent_instrument_type_id_fkey"
    FOREIGN KEY (instrument_type_id) REFERENCES musical_instrument(id) ON DELETE CASCADE NOT DEFERRABLE;

-- FK for parent_detail
ALTER TABLE ONLY "parent_detail" ADD CONSTRAINT "parent_detail_parent_id_fkey"
    FOREIGN KEY (parent_id) REFERENCES parent(id) ON DELETE CASCADE NOT DEFERRABLE;
ALTER TABLE ONLY "parent_detail" ADD CONSTRAINT "parent_detail_student_id_fkey"
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE NOT DEFERRABLE;

-- FK for parent_email
ALTER TABLE ONLY "parent_email" ADD CONSTRAINT "parent_email_parent_id_fkey"
    FOREIGN KEY (parent_id) REFERENCES parent(id) ON DELETE CASCADE NOT DEFERRABLE;

-- FK for parent_phone
ALTER TABLE ONLY "parent_phone" ADD CONSTRAINT "parent_phone_parent_id_fkey"
    FOREIGN KEY (parent_id) REFERENCES parent(id) ON DELETE CASCADE NOT DEFERRABLE;

-- FK for person_email
ALTER TABLE ONLY "person_email" ADD CONSTRAINT "person_email_person_id_fkey"
    FOREIGN KEY (person_id) REFERENCES person(id) ON DELETE CASCADE NOT DEFERRABLE;

-- FK for person_phone
ALTER TABLE ONLY "person_phone" ADD CONSTRAINT "person_phone_person_id_fkey"
    FOREIGN KEY (person_id) REFERENCES person(id) ON DELETE CASCADE NOT DEFERRABLE;

-- FK for student
ALTER TABLE ONLY "student" ADD CONSTRAINT "student_person_id_fkey"
    FOREIGN KEY (person_id) REFERENCES person(id) NOT DEFERRABLE;

-- FK for student_sibling
ALTER TABLE ONLY "student_sibling" ADD CONSTRAINT "student_sibling_sibling_student_id_fkey"
    FOREIGN KEY (sibling_student_id) REFERENCES student(id) ON DELETE CASCADE NOT DEFERRABLE;
ALTER TABLE ONLY "student_sibling" ADD CONSTRAINT "student_sibling_student_id_fkey"
    FOREIGN KEY (student_id) REFERENCES student(id)  ON DELETE CASCADE NOT DEFERRABLE;

-- VIEWS

-- View to display lesson statistics per month and per year for each lesson category as well as the total number of lessons for all categories/ Task 3
-- The view returns statistics for all years
CREATE VIEW monthly_lesson_statistic AS
SELECT
	group_lesson_statistic.year, 
	group_lesson_statistic.month, 
	COALESCE(group_lessons, 0) AS group_lesson, 
	COALESCE(ensembles, 0) AS ensemble, 
	COALESCE(individual_lessons, 0) AS individual_lesson,
	(COALESCE(group_lessons, 0) + COALESCE(ensembles, 0) + COALESCE(individual_lessons, 0)) AS total	   
FROM (
	  (
	  	SELECT 
			to_char(date, 'Mon') AS month, 
			to_char(date, 'YYYY') AS year,
    		COUNT(*) AS group_lessons
    	FROM group_lesson_time
    	GROUP BY year, month
      ) AS group_lesson_statistic 
      FULL OUTER JOIN
      (
      	SELECT 
      		to_char(date, 'Mon') AS month, 
      		to_char(date, 'YYYY') AS year,
    		COUNT(*) AS ensembles
    	FROM ensemble_time
    	GROUP BY year, month
      ) AS ensemble_statistic
      ON group_lesson_statistic.month = ensemble_statistic.month 
      AND group_lesson_statistic.year = ensemble_statistic.year
      FULL OUTER JOIN
      (
      	SELECT 
      		to_char(date, 'Mon') AS month, 
      		to_char(date, 'YYYY') AS year,
    		COUNT(*) AS individual_lessons
    	FROM (
    			SELECT *
     			FROM individual_lesson
     			INNER JOIN instructor_time_slot
     			ON booked_time_slot_id = instructor_time_slot.id
     		 ) AS i
    	GROUP BY year, month
      ) AS individual_lesson_statistic
     ON group_lesson_statistic.month = individual_lesson_statistic.month 
     AND group_lesson_statistic.year = individual_lesson_statistic.year
     )
ORDER BY 
	to_date(group_lesson_statistic.year, 'YYYY'), 
	to_date(group_lesson_statistic.month, 'Mon');
	
	
-- Materialized view for series of months
CREATE MATERIALIZED VIEW generated_month AS
SELECT 
	to_char(generated_month, 'Mon') AS month
FROM 
	generate_series('2022-01-01'::timestamp, '2022-12-31', '1 month') 
	AS generated_month;

-- Materialized view displaying all instructor lessons
CREATE MATERIALIZED VIEW instructor_lesson AS	
SELECT
	instructor_id,
	(first_name || ' ' || last_name) AS instructor_name,
	date,
	start_time,
	end_time,
	lesson_id
FROM (
	  (
      	SELECT 
      		instructor_id, 
      		group_lesson_id AS lesson_id, 
      		date, 
      		start_time, 
      		end_time 
      	FROM group_lesson
      	INNER JOIN group_lesson_time
      	ON group_lesson.id = group_lesson_time.group_lesson_id
      )
      UNION ALL
      (
      	SELECT 
      		instructor_id, 
      		ensemble_time.ensemble_id AS lesson_id, 
      		date, 
      		start_time, 
      		end_time 
      	FROM ensemble
      	INNER JOIN ensemble_time
      	ON ensemble.id = ensemble_time.ensemble_id
      )
      UNION ALL
      (
      	SELECT 
      		individual_lesson.instructor_id AS instructor_id, 
      		individual_lesson.id AS lesson_id, 
      		date, 
      		start_time, 
      		end_time
      	FROM individual_lesson
      	INNER JOIN instructor_time_slot
      	ON individual_lesson.booked_time_slot_id = instructor_time_slot.id
      )
     ) AS lessons
INNER JOIN instructor
ON lessons.instructor_id = instructor.id
INNER JOIN person
ON instructor.person_id = person.id
ORDER BY 
	instructor_id, 
	date, 
	start_time;
	


-- View that shows all ensembles held during next week and their booking status, sorted by target genre and weekday
CREATE VIEW next_week_ensemble_booking_status AS
SELECT 
	target_genre, 
	to_char(date, 'Day') AS weekday, 
	ens_id,
	CASE 
		WHEN (max_partic - number_of_participants = 1) OR (max_partic - number_of_participants = 2) THEN '1-2 seats left'
	 	WHEN (max_partic = number_of_participants) THEN 'fully booked'
	 	ELSE 'places available'
	END booking_status
FROM (
	SELECT *
	FROM (
		SELECT 
			ens_id, 
			target_genre, 
			COUNT(*) AS number_of_participants, 
			max_partic
		FROM (	 
			SELECT 
				ensemble_participant.ensemble_id AS ens_id, 
				student_id AS student, 
				max_number_of_seats AS max_partic, 
				target_genre
			FROM ensemble
			INNER JOIN ensemble_participant
			ON ensemble.id = ensemble_participant.ensemble_id) AS ensemble_with_participant
		GROUP BY 
			ens_id, 
			max_partic, 
			target_genre
			) AS ensemble_with_counted_participantS		
	INNER JOIN ensemble_time
	ON ensemble_time.ensemble_id = ens_id
	) AS ensemble_with_time
WHERE EXTRACT(WEEK FROM date) = EXTRACT(WEEK FROM NOW()) + 1
ORDER BY 
	target_genre, 
	to_char(date, 'D');			

-- View that shows all rentals together with their instrument types and brands	
CREATE VIEW rental_with_instrument AS
SELECT 	
	rental_id,
	start_date,
	end_date,
	student_id,
	instrument_rental.instrument_id,
	type,
	brand,
	stock_quantity,
	monthly_rental_fee
FROM instrument_rental
INNER JOIN musical_instrument_to_rent
ON instrument_rental.instrument_id = musical_instrument_to_rent.id
INNER JOIN musical_instrument
ON instrument_type_id = musical_instrument.id;	

-- FUNCTIONS

-- Trigger function that checks for 2 rental quota
CREATE OR REPLACE FUNCTION check_for_rental_quota() RETURNS trigger AS $check_for_rental_quota$
DECLARE
	max_rentals CONSTANT integer := 2;
	rental_count integer := 0;
	check_status boolean := false;
BEGIN
	IF TG_OP = 'INSERT' THEN
		check_status := true;
	END IF;
	
	IF check_status THEN
		LOCK TABLE instrument_rental IN EXCLUSIVE MODE;
		
		SELECT INTO rental_count COUNT(*)
		FROM ( 
			SELECT * 
			FROM instrument_rental
			WHERE student_id = NEW.student_id AND end_date > CURRENT_DATE
	 		) AS student_rentals;
	 	
	 	IF rental_count >= max_rentals THEN
	 		RAISE EXCEPTION 'Cannot insert more than % rentals per student.', max_rentals;	
		END IF; 	
	END IF;
	
	RETURN NEW;
END;	
$check_for_rental_quota$ LANGUAGE plpgsql;

-- Create triggers

-- Trigger that prevents from creating more than 2 rentals per student
CREATE TRIGGER check_for_rental_quota 
	BEFORE INSERT ON instrument_rental
	FOR EACH ROW EXECUTE PROCEDURE check_for_rental_quota();
