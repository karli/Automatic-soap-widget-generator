{"description":"A schema for representing news content geoinfo",
	"type":"object",
	"properties":{
		"objects":{
			"type":"array",
			"items":{
				"type":"object",
				"properties":{
					"label":{"type":"string"},
					"n":{"type":"number"},
					"e":{"type":"number"},
					"srs":{"type":"string"}
				}
			}
		}
	}
}