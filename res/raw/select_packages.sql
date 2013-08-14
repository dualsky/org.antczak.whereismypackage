SELECT MAX(id) + 1 AS _id
	,1 AS sortOrder
	,'-1' AS packageNumber
	,COUNT(*) AS courierName
	,1 AS courierCode
	,1 AS monitor
	,'monitor' AS DESC
	,CURRENT_TIMESTAMP AS lastUpdate
	,CURRENT_TIMESTAMP AS addDate
FROM packages
WHERE monitor = 1

UNION

SELECT id
	,2
	,packagenumber
	,couriername
	,couriercode
	,monitor
	,DESC
	,lastUpdate
	,addDate
FROM packages
WHERE monitor = 1

UNION

SELECT MAX(id) + 1
	,3
	,'-2'
	,COUNT(*)
	,1
	,0
	,'nie monitor'
	,CURRENT_TIMESTAMP
	,CURRENT_TIMESTAMP	
FROM packages
WHERE monitor = 0

UNION

SELECT id
,4	
,packagenumber
	,couriername
	,couriercode
	,monitor
	,DESC
	,lastUpdate
	,addDate	
FROM packages
WHERE monitor = 0
ORDER BY monitor DESC
	,sortOrder DESC
	,%s %s