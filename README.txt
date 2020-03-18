1. Bash for (Big) Data Analysis

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
mysql> select surname from players where surname like '%ia%' AND minutes < 200 AND passes > 100;
Empty set (0.00 sec)

MongoDB -

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

3. Find the goalkeepers of teams that played more than four games. List the surname of the goalkeeper, the team, and the number of minutes the goalkeeper played.

SQL -
mysql> select surname, team, minutes from players where team in (select team from teams where games > 4);
+-----------------+-------------+---------+
| surname         | team        | minutes |
+-----------------+-------------+---------+
| Aguero          | Argentina   |     106 |
| Bolatti         | Argentina   |      98 |
| Burdisso        | Argentina   |     341 |
| Demichelis      | Argentina   |     450 |
| Di Maria        | Argentina   |     356 |
| Gutierrez       | Argentina   |     191 |
| Heinze          | Argentina   |     360 |
| Higuain         | Argentina   |     341 |
| Mascherano      | Argentina   |     360 |
| Maxi Rodriguez  | Argentina   |     346 |
| Messi           | Argentina   |     450 |
| Milito          | Argentina   |      91 |
| Otamendi        | Argentina   |     250 |
| Palermo         | Argentina   |      10 |
| Pastore         | Argentina   |      37 |
| Rodriguez       | Argentina   |      90 |
| Romero          | Argentina   |     450 |
| Samuel          | Argentina   |     114 |
| Tevez           | Argentina   |     324 |
| Veron           | Argentina   |     185 |
| Baptista        | Brazil      |      82 |
| Daniel Alves    | Brazil      |     310 |
| Elano           | Brazil      |     140 |
| Fabiano         | Brazil      |     418 |
| Gilberto        | Brazil      |      33 |
| Gilberto Silva  | Brazil      |     450 |
| Grafite         | Brazil      |       5 |
| Josue           | Brazil      |      46 |
| Juan            | Brazil      |     450 |
| Julio Cesar     | Brazil      |     450 |
| Kaka            | Brazil      |     337 |
| Kleberson       | Brazil      |       9 |
| Lucio           | Brazil      |     450 |
| Maicon          | Brazil      |     450 |
| Melo            | Brazil      |     291 |
| Michel Bastos   | Brazil      |     422 |
| Nilmar          | Brazil      |     129 |
| Ramires         | Brazil      |     105 |
| Robinho         | Brazil      |     354 |
| Badstuber       | Germany     |     167 |
| Boateng         | Germany     |     287 |
| Cacau           | Germany     |     132 |
| Friedrich       | Germany     |     540 |
| Gomez           | Germany     |      57 |
| Jansen          | Germany     |      73 |
| Khedira         | Germany     |     517 |
| Kiessling       | Germany     |       7 |
| Klose           | Germany     |     356 |
| Kroos           | Germany     |      50 |
| Lahm            | Germany     |     540 |
| Marin           | Germany     |      29 |
| Mertesacker     | Germany     |     540 |
| Mueller         | Germany     |     383 |
| Neuer           | Germany     |     540 |
| Podolski        | Germany     |     531 |
| Schweinsteiger  | Germany     |     531 |
| Trochowski      | Germany     |     109 |
| Ozil            | Germany     |     497 |
| Addy            | Ghana       |     138 |
| Adiyiah         | Ghana       |      33 |
| Amoah           | Ghana       |      11 |
| Annan           | Ghana       |     510 |
| Appiah          | Ghana       |     105 |
| Asamoah         | Ghana       |     480 |
| Ayew            | Ghana       |     389 |
| Boateng         | Ghana       |     464 |
| Gyan            | Ghana       |     501 |
| Inkoom          | Ghana       |     187 |
| John Mensah     | Ghana       |     420 |
| Jonathan Mensah | Ghana       |     300 |
| Kingson         | Ghana       |     510 |
| Muntari         | Ghana       |     134 |
| Owusu-Abeyie    | Ghana       |      35 |
| Pantsil         | Ghana       |     510 |
| Sarpei          | Ghana       |     463 |
| Tagoe           | Ghana       |     210 |
| Vorsah          | Ghana       |     210 |
| Afellay         | Netherlands |      21 |
| Boulahrouz      | Netherlands |     180 |
| Elia            | Netherlands |      85 |
| Heitinga        | Netherlands |     540 |
| Huntelaar       | Netherlands |      48 |
| Kuyt            | Netherlands |     516 |
| Mathijsen       | Netherlands |     450 |
| Ooijer          | Netherlands |      90 |
| Robben          | Netherlands |     267 |
| Sneijder        | Netherlands |     532 |
| Stekelenburg    | Netherlands |     540 |
| Van der Vaart   | Netherlands |     257 |
| Van der Wiel    | Netherlands |     360 |
| de Jong         | Netherlands |     448 |
| de Zeeuw        | Netherlands |      47 |
| van Bommel      | Netherlands |     540 |
| van Bronckhorst | Netherlands |     540 |
| van Persie      | Netherlands |     479 |
| Alcaraz         | Paraguay    |     390 |
| Barreto         | Paraguay    |     111 |
| Barrios         | Paraguay    |     308 |
| Benitez         | Paraguay    |      83 |
| Bonet           | Paraguay    |     300 |
| Caniza          | Paraguay    |      90 |
| Cardozo         | Paraguay    |     204 |
| Caceres         | Paraguay    |      90 |
| Caceres         | Paraguay    |     354 |
| Da Silva        | Paraguay    |     480 |
| Morel Rodriguez | Paraguay    |     480 |
| Ortigoza        | Paraguay    |      75 |
| Riveros         | Paraguay    |     480 |
| Santa Cruz      | Paraguay    |     312 |
| Santana         | Paraguay    |     120 |
| Torres          | Paraguay    |      82 |
| Valdez          | Paraguay    |     337 |
| Vera            | Paraguay    |     414 |
| Veruen          | Paraguay    |      90 |
| Villar          | Paraguay    |     480 |
| Alonso          | Spain       |     506 |
| Arbeloa         | Spain       |      13 |
| Busquets        | Spain       |     511 |
| Capdevila       | Spain       |     540 |
| Casillas        | Spain       |     540 |
| Fabregas        | Spain       |      94 |
| Iniesta         | Spain       |     437 |
| Javi Martuenez  | Spain       |      17 |
| Jesus Navas     | Spain       |     118 |
| Juan Mata       | Spain       |      20 |
| Llorente        | Spain       |      31 |
| Marchena        | Spain       |       8 |
| Pedro Rodriguez | Spain       |     116 |
| Pique           | Spain       |     540 |
| Puyol           | Spain       |     534 |
| Ramos           | Spain       |     527 |
| Silva           | Spain       |      66 |
| Torres          | Spain       |     278 |
| Villa           | Spain       |     529 |
| Xavi            | Spain       |     515 |
| Abreu           | Uruguay     |      72 |
| Arevalo Rios    | Uruguay     |     570 |
| Cavani          | Uruguay     |     435 |
| Caceres         | Uruguay     |      90 |
| Eguren          | Uruguay     |       2 |
| Fernandez       | Uruguay     |      75 |
| Fernandez       | Uruguay     |       7 |
| Forlan          | Uruguay     |     564 |
| Fucile          | Uruguay     |     371 |
| Gargano         | Uruguay     |      91 |
| Goduen          | Uruguay     |     315 |
| Gonzalez        | Uruguay     |      63 |
| Lodeiro         | Uruguay     |     109 |
| Lugano          | Uruguay     |     398 |
| Maxi Pereira    | Uruguay     |     570 |
| Muslera         | Uruguay     |     570 |
| Perez           | Uruguay     |     567 |
| Scotti          | Uruguay     |      95 |
| Suarez          | Uruguay     |     452 |
| Victorino       | Uruguay     |     435 |
| Alvaro Pereira  | Uruguay     |     409 |
+-----------------+-------------+---------+
156 rows in set (0.00 sec)

MongoDB -

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