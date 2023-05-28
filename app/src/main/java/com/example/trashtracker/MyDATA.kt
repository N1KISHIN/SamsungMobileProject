package com.example.trashtracker

data class Waste(val name: String = "",
                 var amount: String= "",
                 var amountType: String= "",
                 var time: String= "",
                 var type: String= "")

data class WasteTypeAssociation(val wasteName: String = "",
                                val wasteType: String = "")

data class CompanyData(val name: String = "",
                       val wasteTypes: String = "",
                       val adress: String = "")

data class Recomendations(val name: String,
                          val recommendation: String)