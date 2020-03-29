select surname from players where team like '%ia%' AND minutes < 200 AND passes > 100;
select * from players where shots > 20 ORDER BY shots DESC;
select surname, team, minutes from players where team in (select team from teams where games > 4) and position='goalkeeper';
select count(*) as superstar from players where team in (select team from teams where ranking < 10) and minutes > 350;
select position, avg(passes) from players where position='midfielder' or position='forward' group by position;
select a.team as Team_A, b.team as Team_B, a.goalsFor as goals_for, a.goalsAgainst as goals_against from teams a, teams b where a.goalsFor = b.goalsFor and a.goalsAgainst = b.goalsAgainst and a.team < b.team;
select team, max(goalsFor/goalsAgainst) as ratio from teams group by team order by ratio desc limit 1;
select avg(passes) as avg_passes, team from players where position='defender' group by team having avg(passes)>150 order by avg_passes desc;