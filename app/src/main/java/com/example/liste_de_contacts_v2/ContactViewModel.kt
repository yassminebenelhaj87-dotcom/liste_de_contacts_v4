package com.example.liste_de_contacts_v2

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.liste_de_contacts_v2.data.ContactDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel partagé gérant l'état des contacts.
 * Effectue toutes les opérations de base de données hors du thread principal via coroutines (Dispatchers.IO).
 */
class ContactViewModel(application: Application) : AndroidViewModel(application) {

    private val contactDao = ContactDao(application)

    private val _contacts = MutableLiveData<List<Contact>>()
    val contacts: LiveData<List<Contact>> = _contacts

    init {
        loadContacts()
    }

    fun loadContacts() {
        viewModelScope.launch {
            val list = withContext(Dispatchers.IO) {
                contactDao.getAll()
            }
            _contacts.value = list
        }
    }

    fun searchContacts(query: String) {
        viewModelScope.launch {
            val list = withContext(Dispatchers.IO) {
                if (query.isEmpty()) {
                    contactDao.getAll()
                } else {
                    contactDao.search(query)
                }
            }
            _contacts.value = list
        }
    }

    fun addContact(contact: Contact, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                contactDao.insert(contact)
            }
            loadContacts()
            onComplete?.invoke()
        }
    }

    fun updateContact(contact: Contact, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                contactDao.update(contact)
            }
            loadContacts()
            onComplete?.invoke()
        }
    }

    fun deleteContact(id: Long, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                contactDao.delete(id)
            }
            loadContacts()
            onComplete?.invoke()
        }
    }

    suspend fun getContactById(id: Long): Contact? {
        return withContext(Dispatchers.IO) {
            contactDao.getById(id)
        }
    }
}
