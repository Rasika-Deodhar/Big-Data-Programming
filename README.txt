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