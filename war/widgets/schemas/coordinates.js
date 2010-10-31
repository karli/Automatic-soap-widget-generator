{"description":"A schema for representing geographical coordinates",
	"type":"object",
	"properties":{
		"location":{
			"type":"object",
			"properties":{
				"coordinates":{
					"type":"object",
					"properties":{
						"latitude":{"type":"number"},
						"longitude":{"type":"number"}
					}
				}
			}
		}
	}
}