 SELECT MAX(adddate) + 1 AS _id,
       '-1'             AS packageNumber,
       COUNT(*)         AS courierName,
       1                AS courierCode,
       1                AS monitor,
       'monitor'        AS DESC
FROM   history
WHERE  monitor = 1
UNION
SELECT adddate,
       packagenumber,
       couriername,
       couriercode,
       monitor,
       desc
FROM   history
WHERE  monitor = 1
UNION
SELECT MAX(adddate) + 1,
       '-2',
       COUNT(*),
       1,
       0,
       'nie monitor'
FROM   history
WHERE  monitor = 0
UNION
SELECT adddate,
       packagenumber,
       couriername,
       couriercode,
       monitor,
       desc
FROM   history
WHERE  monitor = 0
ORDER  BY monitor DESC,
          _id DESC