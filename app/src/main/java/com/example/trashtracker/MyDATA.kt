package com.example.trashtracker

data class Waste(val name: String = "",
                 var amount: String= "",
                 var amountType: String= "",
                 var time: String= "",
                 var type: String= "")

data class WasteTypeAssociation(val wasteName: String = "",
                                val wasteType: String = "")

data class CompanyData(
    val name: String = "",
    //val wasteTypes: String = "",
    val adress: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0)

data class Recomendations(val name: String,
                          val recommendation: String)