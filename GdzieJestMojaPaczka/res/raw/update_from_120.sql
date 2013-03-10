INSERT INTO packages (
	packageNumber
	,courierName
	,courierCode
	,addDate
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
	,DATETIME (
		addDate
		,'unixepoch'
		)
	,monitor
	,DESC
FROM history