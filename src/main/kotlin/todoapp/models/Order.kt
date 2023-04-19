package todoapp.models

import kotlinx.serialization.Serializable

@Serializable
data class Order (val number: String, val contents: List<OrderItem>)

@Serializable
data class OrderItem(val item: String, val amount: Int, val price: Double )

val orderStorage = listOf(
    Order("2020-04-06-01", listOf(
    OrderItem("Ham Sandwich", 2, 2.50),
    OrderItem("Water", 1, 1.50),
    OrderItem("beer", 3, 2.30),
    OrderItem("CheeseCake", 1, 3.75)
)),
    Order("2020-04-03-01", listOf(
        OrderItem("CheeseBurger", 1, 8.5),
        OrderItem("Water", 2, 1.50),
        OrderItem("Coke", 2, 1.76),
        OrderItem("Ice cream", 1, 2.35)
    ))
)