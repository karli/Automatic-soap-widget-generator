{"description":"A schema for adding points to a MapCat map widget",
	"type":"object",
	"properties":{
		"points":{
			"type":"array",
			"items":{
				"type":"object",
				"properties":{
					"coordinates":{
						"type":"object",
						"properties":{
							"e":{"type":"number"},
							"n":{"type":"number"},
							"srs":{"type":"string"}
						}
					},
					"label":{"type":"string"}
				}
			}
		},
		"options":{
			"type":"object",
			"properties":{
				"clear":{"type":"boolean"},
				"center":{"type":"boolean"},
				"style":{
					"type":"object",
					"properties":{
						"color":{"type":"string"},
						"alpha":{"type":"integer"},
						"symbol":{"type":"string"}
					}
				}
			}
		}
	}
}