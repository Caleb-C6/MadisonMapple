package com.cs407.myapplication.ui.database

import com.cs407.myapplication.ui.database.ApartmentDetails
import com.cs407.myapplication.ui.database.FloorPlanEntity
import com.cs407.myapplication.ui.database.ApartmentDao

object ApartmentDetails {

    suspend fun preload(dao: ApartmentDao) {

        // Apartments
        val apartments = listOf(
            ApartmentEntity(
                id = 1,
                name = "Waterfront",
                address = "",
                coordinates = "43.07761,-89.39298",
                utilities = "Wifi"
            ),
            ApartmentEntity(
                id = 2,
                name = "Palisade",
                address = "",
                coordinates = "43.07174,-89.39501",
                utilities = "Wifi"
            ),
            ApartmentEntity(
                id = 3,
                name = "Landon",
                address = "",
                coordinates = "",
                utilities = "Wifi"
            ),
            ApartmentEntity(
                id = 4,
                name = "Iota Courts",
                address = "",
                coordinates = "",
                utilities = "Wifi"
            ),
            ApartmentEntity(
                id = 5,
                name = "Aberdeen Apartments",
                address = "",
                coordinates = "43.07321,-89.39356",
                utilities = "Wifi"
            )
        )

        // Floorplans
        val floorPlans = listOf(
            FloorPlanEntity(
                id = 1,
                apartmentId = 1,
                size = "1024 - 1170",
                bedsBath = "3 bed, 2 bath",
                rent = "1576 - 1871"),
            FloorPlanEntity(
                id = 2,
                apartmentId = 1,
                size ="1373 - 1458",
                bedsBath = "5 bed, 2 bath",
                rent = "1450 - 1680"),

            FloorPlanEntity(
                id = 3,
                apartmentId = 2,
                size ="775 - 865",
                bedsBath = "2 bed, 1 bath",
                rent = "1525 - 1588"),
            FloorPlanEntity(
                id = 4,
                apartmentId = 2,
                size ="861 - 904",
                bedsBath ="2 bed, 2 bath",
                rent = "1700 - 1800"),

            FloorPlanEntity(
                id = 5,
                apartmentId = 3,
                size ="300 - 365",
                bedsBath = "Studio",
                rent = "1300 - 1400"),
            FloorPlanEntity(
                id = 6,
                apartmentId = 3,
                size ="498 - 518",
                bedsBath = "1 bed, 1 bath",
                rent = "1500 - 1600"),

            FloorPlanEntity(
                id = 7,
                apartmentId = 4,
                size = "298 - 435",
                bedsBath = "Studio",
                rent = "1750 - 2100"),
            FloorPlanEntity(
                id = 8,
                apartmentId = 4,
                size ="457 - 792",
                bedsBath = "1 bed, 1 bath",
                rent = "1925 - 2205"),

            FloorPlanEntity(
                id = 9,
                apartmentId = 5,
                size = "577 - 643",
                bedsBath = "1 bed, 1 bath",
                rent = "2200 - 2300"),
            FloorPlanEntity(
                id = 10,
                apartmentId = 5,
                size = "657 - 685",
                bedsBath = "2 bed, 1 bath",
                rent = "1450 - 1600")
        )

        apartments.forEach { dao.insertApartment(it) }
        floorPlans.forEach { dao.insertFloorPlan(it) }
    }
}