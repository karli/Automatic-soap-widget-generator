{"description":"A schema for representing geographical coordinates",
	"type":"object",
	"properties":{
		"route":{
			"type":"object",
			"properties":{
				"time":{"type":"number"},
				"places":{
					"type":"array",
					"items":{
						"type":"object",
						"properties":{
							"lat":{"type":"number"},
							"long":{"type":"number"},
							"srs":{"type":"string", "default":"EPSG:4326"},
							"height":{"type":"number"}
						}
					}
				}
			}
		}
	}
}