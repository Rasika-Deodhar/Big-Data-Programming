use football;
   db.players.find({"team":{$regex:/ia/},"minutes":{"$lt":200},"passes":{"$gt":100}},{"surname":1});
   
db.players.find({"shots":{"$gt":20}}).sort({"shots":-1});

db.players.aggregate([{$lookup:{from:"teams",localField:"team",foreignField:"team",as:"Team"}},{$match:{$and:[{"position":"goalkeeper"},{"Team.games":{$gt:4}}]}},{$project:{"surname":1,"team":1,"minutes":1}}]);

db.players.aggregate([{$lookup:{from:'teams', localField:'team', foreignField:'team', as:'Team'}},{$match:{$and:[{"Team.ranking":{$lt:10}},{"minutes":{$gt:350}}]}},{$facet:{count:[{$count:"superstar"}]}}]).toArray();

db.players.aggregate( [ { $match: { $or: [ { position:"forward" }, { position:"midfielder" } ] } },{ $group: {_id:"$position", avg_passes: { $avg: "$passes"} } } ] );

db.getCollection('teams').aggregate([{$lookup:{from:"teams",let:{"bId":"$_id","bTeam":"$team","bgoalsFor":"$goalsFor","bgoalsAgainst":"$goalsAgainst"},pipeline:[{$match:{$and:[{$expr:{$lt:["$_id","$$bId"]}},{$expr:{$ne:["$team","$$bTeam"]}},{$expr:{$eq:["$goalsFor","$$bgoalsFor"]}},{$expr:{$eq:["$goalsAgainst","$$bgoalsAgainst"]}}]}}],as:"team2"}},{$unwind:"$team2"},{$addFields:{"against_team":"$team2.team"}},{$project:{"team":1,"goalsFor":1,"goalsAgainst":1,"against_team":1}}]).pretty();

db.teams.aggregate([{$group: {_id:"$team", ratio: { $max : { $divide: ["$goalsFor", "$goalsAgainst"]}}}},{$sort:{ratio:-1}},{$project:{team:1, ratio:1}}, {$group:{_id:"$team", first:{$first:"$$ROOT"}}}, {$project:{first:1}}]);

db.players.aggregate( [ { $match: {  position:"defender"  } },{ $group: {_id:"$team", avg_passes: { $avg: "$passes"} } }, {$match:{$expr:{$gt:["$avg_passes", 150]}}}, {$sort: {avg_passes:-1}} ] );