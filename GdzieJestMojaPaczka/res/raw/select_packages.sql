SELECT MAX(id) + 1 AS _id
	,'-1' AS packageNumber
	,COUNT(*) AS courierName
	,1 AS courierCode
	,1 AS monitor
	,'monitor' AS DESC
FROM packages
WHERE monitor = 1

UNION

SELECT id
	,packagenumber
	,couriername
	,couriercode
	,monitor
	,DESC
FROM packages
WHERE monitor = 1

UNION

SELECT MAX(id) + 1
	,'-2'
	,COUNT(*)
	,1
	,0
	,'nie monitor'
FROM packages
WHERE monitor = 0

UNION

SELECT id
	,packagenumber
	,couriername
	,couriercode
	,monitor
	,DESC
FROM packages
WHERE monitor = 0
ORDER BY monitor DESC
	,_id DESC