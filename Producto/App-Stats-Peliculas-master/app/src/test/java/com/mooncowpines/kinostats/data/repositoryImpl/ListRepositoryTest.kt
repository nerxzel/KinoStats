package com.mooncowpines.kinostats.data.repositoryImpl

import com.mooncowpines.kinostats.domain.model.MovieList
import com.mooncowpines.kinostats.domain.repository.ListRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ListRepositoryFakeTest {

    @Test
    fun cp37_createMultipleLists_shouldStoreAndRetrieveAllSuccessfully() = runBlocking {
        val fakeDatabase = mutableListOf<MovieList>()
        val listRepositoryMock = mockk<ListRepository>()

        coEvery { listRepositoryMock.createList(any(), any()) } answers {
            val listName = secondArg<String>()
            val newList = MovieList(
                id = fakeDatabase.size.toLong() + 1L,
                name = listName,
                movieCount = 0,
                movies = emptyList(),
                isWatchList = false
            )
            fakeDatabase.add(newList)
            true
        }

        coEvery { listRepositoryMock.getListsByUser(any()) } answers {
            fakeDatabase
        }

        val dummyUserId = 1L
        listRepositoryMock.createList(dummyUserId, "Por Ver")
        listRepositoryMock.createList(dummyUserId, "Favoritas")

        val retrievedLists = listRepositoryMock.getListsByUser(dummyUserId)

        assertEquals("El repositorio debe contener exactamente 2 listas", 2, retrievedLists!!.size)
        assertEquals("La primera lista debe ser la correcta", "Por Ver", retrievedLists[0].name)
        assertEquals("La segunda lista debe ser la correcta", "Favoritas", retrievedLists[1].name)
    }
}