-- All views presented in this script are already created in create_database.sql and are only shown
-- here to make it easier for the reader to understand the queries performed on them. The reader does
-- not need to create them again.

-- QUERY 1
--
-- A series of months, needed to have empty rows for months that do not have any lessons
CREATE MATERIALIZED VIEW generated_month AS
SELECT
    to_char(generated_month, 'Mon') AS month
FROM
    generate_series('2022-01-01'::timestamp, '2022-12-31', '1 month')
    AS generated_month;

-- View to display lesson statistics per month and per year for each lesson category as well as the total number of lessons for all categories/ Task 3
-- The view returns statistics for all years
CREATE VIEW monthly_lesson_statistic AS
SELECT
    group_lesson_statistic.year,
    group_lesson_statistic.month,
    COALESCE(group_lessons, 0) AS group_lesson,
    COALESCE(ensembles, 0) AS ensemble,
    COALESCE(individual_lessons, 0) AS individual_lesson,
    ( COALESCE(group_lessons, 0) + COALESCE(ensembles, 0) + COALESCE(individual_lessons, 0) ) AS total
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

-- Query to select the number of lessons per category per year (year should be specified in the condition)/ Task 3, query 1
SELECT *
FROM (
       SELECT
            generated_month.month,
            COALESCE(group_lesson, 0) AS group_lesson,
            COALESCE(ensemble, 0) AS ensemble,
            COALESCE(individual_lesson, 0) AS individual_lesson,
            COALESCE(total, 0) AS total
       FROM (
              SELECT *
              FROM monthly_lesson_statistic
              WHERE year = '2022') AS spec_year
              RIGHT JOIN generated_month
              ON spec_year.month = generated_month.month) AS year_statistic;

-- QUERY 2
--
-- Query to select the average number of lessons per month for the specified year/ Task 3, query 2
SELECT
    TRUNC(AVG(group_lesson), 0) AS avg_group_lesson,
    TRUNC(AVG(ensemble), 0) AS avg_ensemble,
    TRUNC(AVG(individual_lesson), 0) AS avg_individual_lesson,
    TRUNC(AVG(total), 0) AS avg_total
FROM (
       SELECT
           COALESCE(group_lesson, 0) AS group_lesson,
           COALESCE(ensemble, 0) AS ensemble,
           COALESCE(individual_lesson, 0) AS individual_lesson,
           COALESCE(total, 0) AS total
       FROM (
              SELECT *
              FROM monthly_lesson_statistic
              WHERE year = '2022' ) AS spec_year
              RIGHT JOIN generated_month
              ON spec_year.month = generated_month.month) AS year_statistic;


-- QUERY 3
--
-- Create view displaying all instructor lessons
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

-- Query to select the number of given lessons per instructor for the next month. The query can
-- easily be changed to the current month by removing " + 1" that comes after "MONTH FROM NOW()".
-- The reason that I selected the next month is that the school does not give any lessons in August.
SELECT
    instructor_id,
    instructor_name,
    COUNT(*) AS number_of_lessons
FROM instructor_lesson
WHERE EXTRACT(YEAR FROM date) = EXTRACT(YEAR FROM NOW())
  AND EXTRACT(MONTH FROM date) = EXTRACT(MONTH FROM NOW()) + 1
  AND EXTRACT(DAY FROM date) < EXTRACT(DAY FROM NOW())
GROUP BY
    instructor_id,
    instructor_name
HAVING COUNT(*) > 2
ORDER BY number_of_lessons;

-- QUERY 4
--
-- Create view that shows all ensembles held during the next week and their booking status
-- (In order to select the booking status for ensembles held in 2 or 3 weeks, the integer
-- in the EXTRACT(WEEK FROM NOW()) + 1 statement has to be changed accordingly).
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

-- Select the results
SELECT *
FROM next_week_ensemble_booking_status;