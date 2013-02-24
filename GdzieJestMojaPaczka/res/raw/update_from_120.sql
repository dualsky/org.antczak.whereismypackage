INSERT INTO packages (
	packageNumber
	,courierName
	,courierCode
	,lastUpdate
	,monitor
	,DESC
	)
SELECT packageNumber
	,courierName
	,courierCode
	,DATETIME (
		addDate
		,'unixepoch'
		)
	,monitor
	,DESC
FROM history