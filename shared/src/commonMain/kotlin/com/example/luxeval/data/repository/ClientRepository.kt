package com.example.luxeval.data.repository

import com.example.luxeval.data.database.daos.ClientDao
import com.example.luxeval.data.database.entities.ClientEntity
import com.example.luxeval.data.mappers.toDomain
import com.example.luxeval.data.mappers.toEntity
import com.example.luxeval.domain.Client
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ClientRepository(
    private val dao: ClientDao
) {

    suspend fun saveClient(client: Client) {
        dao.insert(client.toEntity())
    }

    suspend fun updateClient(client: Client) {
        dao.update(client.toEntity())
    }

    suspend fun deleteClient(client: Client) {
        dao.delete(client.toEntity())
    }

    suspend fun deleteClient(clientId: String) {
        dao.deleteById(clientId)
    }

    suspend fun getClient(clientId: String): Client? =
        dao.getById(clientId)?.toDomain()

    fun observeClients(): Flow<List<Client>> =
        dao.observeClients().map { entities ->
            entities.map(ClientEntity::toDomain)
        }
}