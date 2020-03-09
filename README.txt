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



