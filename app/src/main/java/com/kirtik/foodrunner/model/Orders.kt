package com.kirtik.foodrunner.model

data class Orders (
    val resName : String,
    val orderDate : String,
    val itemList : List<OrderItem>
)

