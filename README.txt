1.Bash for (Big) Data Analysis

1.1 how many lines of content (no header) is there in the  le?  (tail, wc).
maggy@csserver:~/project$ tail -n+2 crimedata-australia.csv | wc -l
40
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
1.2  create a bash command (using, among other things, sed and wc) to count the number of columns.
maggy@csserver:~/project$ head -1 crimedata-australia.csv | sed 's/[^,]//g' |wc -c
11
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
1.3 For a given city (given as a column number,  e.g.,  10=Sydney),  what is the type of crime on top of the crime list (cat, cut, sort, head)?
maggy@csserver:~/project$ cat crimedata-australia.csv | cut -d ',' -f1,10| tail -n+2 | sort -nr -t',' -k2 | head -n 1 | cut -d ',' -f1
Drugs ? Imported
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
1.4  Find the number of crimes for a given city (given as a column number, e.g., 10=Sydney):  create a bash script that reads all the rows (see previous question) and sums up the crime values

#!/bin/bash
echo "Enter Column Value:"
read column_value

sum=0
col="$(cat "crimedata-australia.csv"| tail -n+2 | cut -d "," -f$column_value)"
for n in $col
do
    sum=$(($sum+$n))
done
echo $sum

Output:
maggy@csserver:~/project$ ./script1.sh
Enter Column Value:
3
139
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
1.5 Same question with the average - look at question 1 to get the number of rows & use tr to remove the empty white spaces and get the number

#!/bin/bash
echo "Enter column value:"
read column_value

total="$(tail -n+2 "crimedata-australia.csv" | wc -l)"
column="$(cat "crimedata-australia.csv" | tail -n+2 | cut -d ',' -f$column_value)"
sum=0

for i in $column
do
  sum=$(($sum+$i))
done

avg=$(($sum/$total))

echo "Avg is = $avg"

Output:
maggy@csserver:~/project$ ./script2.sh
Enter column value:
10
Avg is = 11
maggy@csserver:~/project$ ./script2.sh
Enter column value:
2
Avg is = 14
maggy@csserver:~/project$ ./script2.sh
Enter column value:
3
Avg is = 3
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
1.6 Get the city with the lowest average crime. Create a bash scripts that goes through all the cities, compute the average crime rate and keep only the city with the lowest value. Finally display the city and the average number of crimes.

#!/bin/bash

datafile="crimedata-australia.csv"

min_avg=0.0
min_avg_name="$(cat $datafile | head -1 | cut -d ',' -f2)"

total="$(tail -n+2 $datafile | wc -l)"

for i in {2..10}
do
sum=0
column_name="$(cat $datafile | head -1 | cut -d ',' -f$i)"
column="$(cat $datafile | tail -n+2 | cut -d ',' -f$i)"
echo $columns_num

 for j in $column
 do
   let sum=$(($sum+$j))
 done

 avg=$(echo "scale=2; $sum/$total"|bc -l)

 if [ $i -eq 2 ]
 then
   min_avg=$avg
 else
   if (( $(echo "$min_avg > $avg" | bc -l) ))
   then
     min_avg=$avg
     min_avg_name=$column_name
   fi
 fi
done

echo "Avg is = $min_avg for city = $min_avg_name"

Output:
maggy@csserver:~/project$ ./script3.sh
Avg is = .52 for city = Hobart

--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
2. Data Management

Part 1:
----Download the Players.csv and Teams.csv datasets from Brighspace.
2.1. Describe in a short paragraph the two datasets.

a. Players: 
- This dataset consists of values of various players of a football game. The first field is the last name of the player, followed by the team that player belongs to, position in game, minutes played, shots played, passes made, tackles faced and saves done.
- The data is sorted based on team.
- Each team has 18-20 members. Shots and Tackles played by goalkeeper are 0 or 1. Saves done by other position players are 0, except one goalkeeper - Buffon, Italy. 

b. Teams:
- The data consists of teams of various countries, as mentioned in the Players Database. There are total 32 teams, each having information about - their rankings, games played, number of wins, draws and losses; also about goals for, goals against; yellow and red cards recieved.
- The data is sorted based on rankings of the teams.

--------------------------------------------------------------------
2.2 Create a database in MySQL and a few tables to represent the dataset. Give the conceptual design and the relational model associated with your database.

Conceptual Design:

Relational Model:


----------------------------------------------------------------------
2.3 Populate the database using a Bash script

#loading players data
datafile="Players.csv"
rows="$(cat $datafile|wc -l)"
rows=$((rows+1))
i=2
while [ "$i" -le "$rows" ];
do
        row="$(cat $datafile | head -n $i | tail -1)"

        _surname="$(echo $row | cut -d ',' -f1)"
        _team="$(echo $row | cut -d ',' -f2)"
        _position="$(echo $row | cut -d ',' -f3)"
        _minutes="$(echo $row | cut -d ',' -f4)"
        _shots="$(echo $row | cut -d ',' -f5)"
        _passes="$(echo $row | cut -d ',' -f6)"
        _tackles="$(echo $row | cut -d ',' -f7)"
        _saves="$(echo $row | cut -d ',' -f8)"

        sudo mysql -h localhost -D "football" -e "INSERT INTO players(surname, team, position, minutes, shots, passes, tackles, saves) VALUES (\"${_surname/\'/''}\", \"${_team/\'/''}\", \"${_position/\'/''}\", $_minutes, $_shots, $_passes, $_tackles, $_saves); "

        i=$(($i+1))

done

#loading teams data
datafile="Teams.csv"
rows="$(cat $datafile|wc -l)"
rows=$(($rows+1))
i=2
while [ "$i" -le "$rows" ];
do

        row="$(cat $datafile | head -n $i | tail -1)"

        _team="$(echo $row | cut -d ',' -f1)"
        _ranking="$(echo $row | cut -d ',' -f2)"
        _games="$(echo $row | cut -d ',' -f3)"
        _wins="$(echo $row | cut -d ',' -f4)"
        _draws="$(echo $row | cut -d ',' -f5)"
        _losses="$(echo $row | cut -d ',' -f6)"
        _goalsFor="$(echo $row | cut -d ',' -f7)"
        _goalsAgainst="$(echo $row | cut -d ',' -f8)"
        _yellowCards="$(echo $row | cut -d ',' -f9)"
        _redCards="$(echo $row | cut -d ',' -f10)"

        sudo mysql -h localhost -D "football" -e "INSERT INTO teams(team, ranking, games, wins, draws, losses, goalsFor, goalsAgainst, yellowCards, redCards) VALUES (\"${_team/\'/''}\", $_ranking, $_games, $_wins, $_draws, $_losses, $_goalsFor, $_goalsAgainst, $_yellowCards, $_redCards); "

        i=$(($i+1))
done

----------------------------------------------------------------------
2.4 Create MongoDB collections with every line in the CSV files as an entry/document. Populate the collections with the content of the CSV files (using a script).

mongoimport -d football -c players --type csv --file Players.csv --headerline

mongoimport -d football -c teams --type csv --file Teams.csv --headerline
-------------------------------------------------------------------------------

Part 2:
---------------Now answer the following questions using SQL and MongoDB queries/searches.


1. What player on a team with "ia" in the team name played less than 200 minutes and made more than 100 passes? Return the player surname.

Connecting using bash:
sudo mysql -h localhost -e "show databases"
sudo mysql -h localhost -e "use football"

SQL -
mysql> select surname from players where team like '%ia%' AND minutes < 200 AND passes > 100;
+------------+
| surname    |
+------------+
| Kuzmanovic |
+------------+
1 row in set (0.00 sec)

MongoDB -
> db.players.find({
...     "team": {
... $regex: /ia/
... },
...     "minutes": {
...         "$lt": 200
...     },
...     "passes": {
...         "$gt": 100
...     }
... }, {
...     "surname": 1
... });
{ "_id" : ObjectId("5e72629ea6830e68f96a2dab"), "surname" : "Kuzmanovic" }

2. Find all players who made more than 20 shots. Return all player information in descending order of shots made.

SQL -
mysql> select * from players where shots > 20 ORDER BY shots DESC ;
+---------+-----------+----------+---------+-------+--------+---------+-------+
| surname | team      | position | minutes | shots | passes | tackles | saves |
+---------+-----------+----------+---------+-------+--------+---------+-------+
| Gyan    | Ghana     | forward  |     501 |    27 |    151 |       1 |     0 |
| Villa   | Spain     | forward  |     529 |    22 |    169 |       2 |     0 |
| Messi   | Argentina | forward  |     450 |    21 |    321 |      10 |     0 |
+---------+-----------+----------+---------+-------+--------+---------+-------+
3 rows in set (0.00 sec)

MongoDB -
> db.players.find({
...     "shots": {
...         "$gt": 20
...     }
... }).sort({
...     "shots": -1
... });
{ "_id" : ObjectId("5e72629ea6830e68f96a2cc4"), "surname" : "Gyan", "team" : "Ghana", "position" : "forward", "minutes" : 501, "shots" : 27, "passes" : 151, "tackles" : 1, "saves" : 0 }
{ "_id" : ObjectId("5e72629ea6830e68f96a2e13"), "surname" : "Villa", "team" : "Spain", "position" : "forward", "minutes" : 529, "shots" : 22, "passes" : 169, "tackles" : 2, "saves" : 0 }
{ "_id" : ObjectId("5e72629ea6830e68f96a2c18"), "surname" : "Messi", "team" : "Argentina", "position" : "forward", "minutes" : 450, "shots" : 21, "passes" : 321, "tackles" : 10, "saves" : 0 }

3. Find the goalkeepers of teams that played more than four games. List the surname of the goalkeeper, the team, and the number of minutes the goalkeeper played.

SQL -
mysql> select surname, team, minutes from players where team in (select team from teams where games > 4) and position='goalkeeper';
+--------------+-------------+---------+
| surname      | team        | minutes |
+--------------+-------------+---------+
| Romero       | Argentina   |     450 |
| Julio Cesar  | Brazil      |     450 |
| Neuer        | Germany     |     540 |
| Kingson      | Ghana       |     510 |
| Stekelenburg | Netherlands |     540 |
| Villar       | Paraguay    |     480 |
| Casillas     | Spain       |     540 |
| Muslera      | Uruguay     |     570 |
+--------------+-------------+---------+
8 rows in set (0.00 sec)


MongoDB -
> db.players.aggregate([
... {
...    $lookup:
...      {
...        from: "teams",
...        localField: "team",
...    foreignField: "team",
...        as: "Team"
...      }
... },
... {
... $match:
... {
... $and:
... [
... {
... "position": "goalkeeper"
... },
... {
... "Team.games": {$gt:4}
... }
ct:
        {
    ]
... }
... },
... {
... $project:
... {
... "surname": 1,
... "team": 1,
... "minutes": 1
... }
... }
... ]);
{ "_id" : ObjectId("5e72629ea6830e68f96a2c1e"), "surname" : "Romero", "team" : "Argentina", "minutes" : 450 }
{ "_id" : ObjectId("5e72629ea6830e68f96a2c3e"), "surname" : "Julio Cesar", "team" : "Brazil", "minutes" : 450 }
{ "_id" : ObjectId("5e72629ea6830e68f96a2cb7"), "surname" : "Neuer", "team" : "Germany", "minutes" : 540 }
{ "_id" : ObjectId("5e72629ea6830e68f96a2cc8"), "surname" : "Kingson", "team" : "Ghana", "minutes" : 510 }
{ "_id" : ObjectId("5e72629ea6830e68f96a2d46"), "surname" : "Stekelenburg", "team" : "Netherlands", "minutes" : 540 }
{ "_id" : ObjectId("5e72629ea6830e68f96a2d92"), "surname" : "Villar", "team" : "Paraguay", "minutes" : 480 }
{ "_id" : ObjectId("5e72629ea6830e68f96a2e05"), "surname" : "Casillas", "team" : "Spain", "minutes" : 540 }
{ "_id" : ObjectId("5e72629ea6830e68f96a2e37"), "surname" : "Muslera", "team" : "Uruguay", "minutes" : 570 }


4. How many players who play on a team with ranking <10 played more than 350 minutes? Return one number in a column named 'superstar'

SQL -
mysql> select count(*) as superstar from players where team in (select team from teams where ranking < 10) and minutes > 350;
+-----------+
| superstar |
+-----------+
|        54 |
+-----------+
1 row in set (0.00 sec)

MongoDB -
> db.players.aggregate([{$lookup:{from:'teams', localField:'team', foreignField:'team', as:'Team'}},{$match:{$and:[{"Team.ranking":{$lt:10}},{"minutes":{$gt:350}}]}},{$facet:{count:[{$count:"superstar"}]}}]).toArray();
[ { "count" : [ { "superstar" : 54 } ] } ]

5. What is the average number of passes made by forwards? By midfelders? Write one query that gives both values with the corresponding position.

SQL -
mysql> select position, avg(passes) from players where position='midfielder' or position='forward' group by position;
+------------+-------------+
| position   | avg(passes) |
+------------+-------------+
| forward    |     50.8252 |
| midfielder |     95.2719 |
+------------+-------------+
2 rows in set (0.00 sec)

MongoDB -
> db.players.aggregate( [ { $match: { $or: [ { position:"forward" }, { position:"midfielder" } ] } },{ $group: {_id:"$position", avg_passes: { $avg: "$passes"} } } ] )
{ "_id" : "forward", "avg_passes" : 50.82517482517483 }
{ "_id" : "midfielder", "avg_passes" : 95.2719298245614 }


6. Find all pairs of teams who have the same number of goalsFor as each other and the same number of goalsAgainst as each other. Return the teams and numbers of goalsFor and goalsAgainst. Make sure to return each pair only once.

SQL -
mysql> select a.team as Team_A, b.team as Team_B, a.goalsFor as goals_for, a.goalsAgainst as goals_against from teams a, teams b where a.goalsFor = b.goalsFor and a.goalsAgainst = b.goalsAgainst and a.team < b.team ;
+-----------+--------------+-----------+---------------+
| Team_A    | Team_B       | goals_for | goals_against |
+-----------+--------------+-----------+---------------+
| Chile     | England      |         3 |             5 |
| Cameroon  | Greece       |         2 |             5 |
| Italy     | Mexico       |         4 |             5 |
| England   | Nigeria      |         3 |             5 |
| Chile     | Nigeria      |         3 |             5 |
| Australia | Denmark      |         3 |             6 |
| England   | South Africa |         3 |             5 |
| Chile     | South Africa |         3 |             5 |
| Nigeria   | South Africa |         3 |             5 |
+-----------+--------------+-----------+---------------+
9 rows in set (0.00 sec)

MongoDB -
> db.getCollection('teams').aggregate([
...    {
...       $lookup:{
...          from:"teams",
...          let:{"bId":"$_id","bTeam":"$team","bgoalsFor":"$goalsFor","bgoalsAgainst":"$goalsAgainst"},
...          pipeline:[
...             {
...                $match:{
...                   $and:[
...                      {$expr:{$lt:["$_id","$$bId"]}},
...                      {$expr:{$ne:["$team","$$bTeam"]}},
...                      {$expr:{$eq:["$goalsFor","$$bgoalsFor"]}},
...                      {$expr:{$eq:["$goalsAgainst","$$bgoalsAgainst"]}}
...                   ]
...                }
...             }
...          ],
...          as:"team2"
...       }
...    },
...    {$unwind:"$team2"},
...    {$addFields:{"against_team":"$team2.team"}},
...    {$project:{"team":1,"goalsFor":1,"goalsAgainst":1,"against_team":1}}
... ]).pretty();
{
        "_id" : ObjectId("5e72629ea6830e68f96a2e61"),
        "team" : "Mexico",
        "goalsFor" : 4,
        "goalsAgainst" : 5,
        "against_team" : "Italy"
}
{
        "_id" : ObjectId("5e72629ea6830e68f96a2e63"),
        "team" : "Cameroon",
        "goalsFor" : 2,
        "goalsAgainst" : 5,
        "against_team" : "Greece"
}
{
        "_id" : ObjectId("5e72629ea6830e68f96a2e65"),
        "team" : "Nigeria",
        "goalsFor" : 3,
        "goalsAgainst" : 5,
        "against_team" : "England"
}
{
        "_id" : ObjectId("5e72629ea6830e68f96a2e67"),
        "team" : "Chile",
        "goalsFor" : 3,
        "goalsAgainst" : 5,
        "against_team" : "England"
}
{
        "_id" : ObjectId("5e72629ea6830e68f96a2e67"),
        "team" : "Chile",
        "goalsFor" : 3,
        "goalsAgainst" : 5,
        "against_team" : "Nigeria"
}
{
        "_id" : ObjectId("5e72629ea6830e68f96a2e6d"),
        "team" : "Denmark",
        "goalsFor" : 3,
        "goalsAgainst" : 6,
        "against_team" : "Australia"
}
{
        "_id" : ObjectId("5e72629ea6830e68f96a2e72"),
        "team" : "South Africa",
        "goalsFor" : 3,
        "goalsAgainst" : 5,
        "against_team" : "England"
}
{
        "_id" : ObjectId("5e72629ea6830e68f96a2e72"),
        "team" : "South Africa",
        "goalsFor" : 3,
        "goalsAgainst" : 5,
        "against_team" : "Nigeria"
}
{
        "_id" : ObjectId("5e72629ea6830e68f96a2e72"),
        "team" : "South Africa",
        "goalsFor" : 3,
        "goalsAgainst" : 5,
        "against_team" : "Chile"
}


7. Which team has the highest ratio of goalsFor to goalsAgainst?

SQL -
mysql> select team, max(goalsFor/goalsAgainst) as ratio from teams group by team order by ratio desc limit 1;
+----------+--------+
| team     | ratio  |
+----------+--------+
| Portugal | 7.0000 |
+----------+--------+
1 row in set (0.00 sec)

MongoDB -
> db.teams.aggregate([{$group: {_id:"$team", ratio: { $max : { $divide: ["$goalsFor", "$goalsAgainst"]}}}},{$sort:{ratio:-1}},{$project:{team:1, ratio:1}}, {$group:{_id:"$team", first:{$first:"$$ROOT"}}}, {$project:{first:1}}])
{ "_id" : null, "first" : { "_id" : "Portugal", "ratio" : 7 } }

8. Find all teams whose defenders averaged more than 150 passes. Return the team and average number of passes by defenders, in descending order of average passes.

SQL -
mysql> select avg(passes) as avg_passes, team from players where position='defender' group by team having avg(passes)>150 order by avg_passes desc;
+------------+-------------+
| avg_passes | team        |
+------------+-------------+
|   213.0000 | Spain       |
|   190.0000 | Brazil      |
|   189.8333 | Germany     |
|   182.5000 | Netherlands |
|   152.1429 | Mexico      |
+------------+-------------+
5 rows in set (0.00 sec)


MongoDB -
> db.players.aggregate( [ { $match: {  position:"defender"  } },{ $group: {_id:"$team", avg_passes: { $avg: "$passes"} } }, {$match:{$expr:{$gt:["$avg_passes", 150]}}}, {$sort: {avg_passes:-1}} ] )
{ "_id" : "Spain", "avg_passes" : 213 }
{ "_id" : "Brazil", "avg_passes" : 190 }
{ "_id" : "Germany", "avg_passes" : 189.83333333333334 }
{ "_id" : "Netherlands", "avg_passes" : 182.5 }
{ "_id" : "Mexico", "avg_passes" : 152.14285714285714 }
>
