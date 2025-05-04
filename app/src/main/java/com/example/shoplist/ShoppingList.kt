package com.example.shoplist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class ShoppingItem(
    val id: Int,
    val name: String,
    val quantity: Int,
    val isEditing: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListApp() {
    var sItems by remember { mutableStateOf(listOf<ShoppingItem>()) }
    var idCounter by remember { mutableIntStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("") }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Button(
                onClick = { showDialog = true },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Add Item")
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(sItems) { item ->
                    if (item.isEditing) {
                        ShoppingItemEditor(
                            item = item,
                            onEditComplete = { editedName, editedQuantity ->
                                sItems = sItems.map {
                                    if (it.id == item.id) {
                                        it.copy(
                                            name = editedName,
                                            quantity = editedQuantity,
                                            isEditing = false
                                        )
                                    } else {
                                        it.copy(isEditing = false)
                                    }
                                }
                            }
                        )
                    } else {
                        ShoppingListItem(
                            item = item,
                            onEditClick = {
                                sItems = sItems.map {
                                    it.copy(isEditing = it.id == item.id)
                                }
                            },
                            onDeleteClick = {
                                sItems = sItems - item
                            }
                        )
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Add Shopping Item") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = itemName,
                            onValueChange = { itemName = it },
                            label = { Text("Item name") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                        OutlinedTextField(
                            value = itemQuantity,
                            onValueChange = { itemQuantity = it },
                            label = { Text("Quantity") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                },
                confirmButton = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(onClick = { showDialog = false }) {
                            Text("Cancel")
                        }
                        Button(onClick = {
                            val quantity = itemQuantity.toIntOrNull() ?: 1
                            if (itemName.isNotBlank()) {
                                val newItem = ShoppingItem(
                                    id = idCounter++,
                                    name = itemName,
                                    quantity = quantity
                                )
                                sItems = sItems + newItem
                                itemName = ""
                                itemQuantity = ""
                                showDialog = false
                            }
                        }) {
                            Text("Add")
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun ShoppingListItem(
    item: ShoppingItem,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(border = BorderStroke(2.dp, Color.Cyan), shape = RoundedCornerShape(20))
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Name : ${item.name}", modifier = Modifier.padding(8.dp))
            Text(text = "Qty : ${item.quantity}", modifier = Modifier.padding(8.dp))
        }
        Row(modifier = Modifier.padding(8.dp)) {
            IconButton(onClick = onEditClick) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun ShoppingItemEditor(
    item: ShoppingItem,
    onEditComplete: (String, Int) -> Unit
) {
    var editedName by remember { mutableStateOf(item.name) }
    var editedQuantity by remember { mutableStateOf(item.quantity.toString()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column {
            BasicTextField(
                value = editedName,
                onValueChange = { editedName = it },
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp)
            )
            BasicTextField(
                value = editedQuantity,
                onValueChange = { editedQuantity = it },
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp)
            )
        }
        Button(onClick = {
            val qty = editedQuantity.toIntOrNull() ?: 1
            onEditComplete(editedName, qty)
        }) {
            Text("Save")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShoppingListAppPreview() {
    ShoppingListApp()
}
