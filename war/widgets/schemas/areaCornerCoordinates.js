{"description":"A schema for representing newsarea corner coordinates",
	"type":"object",
	"properties":{
		"bottomLeft":{
			"type":"object",
			"properties":{
				"lat":{"type":"number"},
				"lng":{"type":"number"},
				"srs":{"type":"string"}
			}
		},
		"upperRight":{
			"type":"object",
			"properties":{
				"lat":{"type":"number"},
				"lng":{"type":"number"},
				"srs":{"type":"string"}
			}
		}
	}
}