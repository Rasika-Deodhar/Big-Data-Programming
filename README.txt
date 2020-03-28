----------------------------------
1.Bash for (Big) Data Analysis	
----------------------------------
1. How many lines of content (no header) is there in the file? (tail, wc).
Steps:-
1. From you linux terminal, go to folder Bash_Scripts.
2. Write command chmod 777 query_1 or chmod u+x query_1.sh
3. If authorization error occurs, append sudo to the above commands.
4. Now throw command ./query_1.sh
5. Ouput would be generated as 40.

2. Create a bash command (using, among other things, sed and wc) to count the number of columns.
Steps:-
1. In terminal, go to Bash_Scripts directory, then throw command sudo chmod 777 query_2.sh
2. Now throw command ./query_2.sh
3. Ouput would be generated as 11.

3. For a given city (given as a column number, e.g., 10=Sydney), what is the type of crime on top of the crime list (cat, cut, sort, head)?
Steps:- (calculated for default column number 10)
1. In terminal, go to Bash_Scripts directory, then throw command sudo chmod 777 query_3.sh
2. Now throw command ./query_3.sh
3. Ouput would be generated as Drugs ? Imported.

4. Find the number of crimes for a given city (given as a column number, e.g., 10=Sydney): Create a bash script that reads all the rows (see previous question) and sums up the crime values.
Steps:-
1. In terminal, go to Bash_Scripts directory, then throw command sudo chmod 777 script_1.sh
2. Now throw command ./script_1.sh

5. Same question with the average - look at question 1 to get the number of rows & use tr to remove the empty white spaces and get the number.
Steps:-
1. In terminal, go to Bash_Scripts directory, then throw command sudo chmod 777 script_2.sh
2. Now throw command ./script_2.sh

6. Get the city with the lowest average crime. Create a bash scripts that goes through all the cities, compute the average crime rate and keep only the city with the lowest value. Finally display the city and the average number of crimes.
Steps:-
1. In terminal, go to Bash_Scripts directory, then throw command sudo chmod 777 script_3.sh
2. Now throw command ./script_3.sh


------------------------------
2. Data Management
------------------------------
Part 1: Download the Players.csv and Teams.csv datasets from Brightspace.
------------------------------
3. Populate the database using a Bash script.

For SQL:-
Steps:-
1. In terminal, go to Bash_Scripts directory, then throw command sudo chmod 777 script_4.sh
2. Now throw command ./script_4.sh 

For MongoDB:-
Steps:-
1. In terminal, go to Bash_Scripts directory, then throw command sudo chmod 777 script_5.sh
2. Now throw command ./script_5.sh 

------------------------------
Part 2: Now answer the following questions using SQL and MongoDB queries/searches.
------------------------------
1. What player on a team with "ia" in the team name played less than 200 minutes and made more than 100 passes? Return the player surname.
Steps:-
1. In terminal, go to SQL_Queries directory, then throw command sudo chmod 777 sql_2_1.sql
2. Now throw command sudo mysql -h localhost football < sql_2_1.sql 
3. In terminal, go to Mongo_Queries directory, then throw command sudo chmod 777 mongo_2_1.js
4. Now throw command sudo mongo < mongo_2_1.js 

2. Find all players who made more than 20 shots. Return all player information in descending order of shots made.
Steps:-
1. In terminal, go to SQL_Queries directory, then throw command sudo chmod 777 sql_2_2.sql
2. Now throw command sudo mysql -h localhost football < sql_2_2.sql 
3. In terminal, go to Mongo_Queries directory, then throw command sudo chmod 777 mongo_2_1.js
4. Now throw command sudo mongo < mongo_2_1.js 

3. Find the goalkeepers of teams that played more than four games. List the surname of the goalkeeper, the team, and the number of minutes the goalkeeper played.
Steps:-
1. In terminal, go to SQL_Queries directory, then throw command sudo chmod 777 sql_2_3.sql
2. Now throw command sudo mysql -h localhost football < sql_2_3.sql 
3. In terminal, go to Mongo_Queries directory, then throw command sudo chmod 777 mongo_2_3.js
4. Now throw command sudo mongo < mongo_2_3.js 

4. How many players who play on a team with ranking <10 played more than 350 minutes? Return one number in a column named 'superstar'
Steps:-
1. In terminal, go to SQL_Queries directory, then throw command sudo chmod 777 sql_2_4.sql
2. Now throw command sudo mysql -h localhost football < sql_2_4.sql 
3. In terminal, go to Mongo_Queries directory, then throw command sudo chmod 777 mongo_2_4.js
4. Now throw command sudo mongo < mongo_2_4.js 

5. What is the average number of passes made by forwards? By midfielders? Write one query that gives both values with the corresponding position.
Steps:-
1. In terminal, go to SQL_Queries directory, then throw command sudo chmod 777 sql_2_5.sql
2. Now throw command sudo mysql -h localhost football < sql_2_5.sql 
3. In terminal, go to Mongo_Queries directory, then throw command sudo chmod 777 mongo_2_5.js
4. Now throw command sudo mongo < mongo_2_5.js 

6. Find all pairs of teams who have the same number of goalsFor as each other and the same number of goalsAgainst as each other. Return the teams and numbers of goalsFor and goalsAgainst. Make sure to return each pair only once.
Steps:-
1. In terminal, go to SQL_Queries directory, then throw command sudo chmod 777 sql_2_6.sql
2. Now throw command sudo mysql -h localhost football < sql_2_6.sql 
3. In terminal, go to Mongo_Queries directory, then throw command sudo chmod 777 mongo_2_6.js
4. Now throw command sudo mongo < mongo_2_6.js 

7. Which team has the highest ratio of goalsFor to goalsAgainst?
Steps:-
1. In terminal, go to SQL_Queries directory, then throw command sudo chmod 777 sql_2_7.sql
2. Now throw command sudo mysql -h localhost football < sql_2_7.sql 
3. In terminal, go to Mongo_Queries directory, then throw command sudo chmod 777 mongo_2_7.js
4. Now throw command sudo mongo < mongo_2_7.js 

8. Find all teams whose defenders averaged more than 150 passes. Return the team and average number of passes by defenders, in descending order of average passes.
Steps:-
1. In terminal, go to SQL_Queries directory, then throw command sudo chmod 777 sql_2_8.sql
2. Now throw command sudo mysql -h localhost football < sql_2_8.sql 
3. In terminal, go to Mongo_Queries directory, then throw command sudo chmod 777 mongo_2_8.js
4. Now throw command sudo mongo < mongo_2_8.js 

----------------------------------
3. Hadoop
----------------------------------
Steps to upload yellow_tripdata_2019-01.csv file in hadoop.
1. Loading file in docker environment -> curl link-of-data
2. Going in hadoop environment ->  docker exec -it namenode bash
3. Then throw command ->  hdfs dfs -copyFromLocal yellow_tripdata_2019-01.csv /dataset
4. Checking if file is uploaded ->  hdfs dfs -ls /dataset

1. What is the average number of passengers per trip in general and per day of the week?
Steps:-
1. 

2. What is the average trip distance in general and per day of the week?

3. What are the most used payment types? Create an ordered list using Hadoop MapReduce or a Bash script.

4. Create a graph (using the output of a MapReduce job) showing the average number of passengers over the day (per hour). Create a version for work days and another for weekend days.

5. Create a graph showing the average trip distance over the day (per hour). Create a version for work days and another for weekend days.

6. Create a graph showing the average number of passengers over the day (per hour). Create a version for work days and another for weekend days.
