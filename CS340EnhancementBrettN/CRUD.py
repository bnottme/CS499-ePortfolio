# -*- coding: utf-8 -*-
from pymongo import MongoClient
from bson.objectid import ObjectId

class AnimalShelter(object):
    """ CRUD operations for Animal collection in MongoDB """

    def __init__(self, username, password):
        # Initializing the MongoClient. This helps to 
        # access the MongoDB databases and collections.
        # This is hard-wired to use the aac database, the 
        # animals collection, and the aac user.
        # Definitions of the connection string variables are
        # unique to the individual Apporto environment.
        #
        # You must edit the connection variables below to reflect
        # your own instance of MongoDB!
        #
        # Connection Variables
        #
        USER = username
        PASS = password
        HOST = 'nv-desktop-services.apporto.com'
        PORT = 32652
        DB = 'AAC'
        COL = 'animals'
        #
        # Initialize Connection
        #
        self.client = MongoClient('mongodb://%s:%s@%s:%d' % (USER,PASS,HOST,PORT))
        self.database = self.client['%s' % (DB)]
        self.collection = self.database['%s' % (COL)]

# Complete this create method to implement the C in CRUD.
    def create(self, data):
        if data is not None:
            try:
                insertValid = self.database.animals.insert_one(data)  # data should be dictionary    
                if insertValid.acknowledged:
                    return True
                return False
            except Exception as e:
                raise Exception(f'Error saving data: {str(e)}')
        else:
            raise Exception("Nothing to save, because data parameter is empty")

# Create method to implement the R in CRUD.
    def read(self, searchData =None):
        try:
            if searchData:
                data = self.database.animals.find(searchData, {"_id": False})
            else:
                data = self.database.animals.find({}, {"_id": False})
            return list(data)
        except Exception as e:
            raise Exception(f"Error reading data: {str(e)}")
            
#Create method to implement the U in CRUD.
    def update(self, searchData, newData):
        if searchData is not None:
            try:
                updateValid = self.database.animals.update_many(searchData, {"$set": newData})
                return updateValid.modified_count
            except Exception as e:
                raise Exception(f"Error updating data: {str(e)}")
        else:
            raise Exception("Nothing to update")
            
#Create method to implement the D in CRUD.
    def delete(self, searchData):
        if searchData is not None:
            try:
                deleteValid = self.database.animals.delete_many(searchData)
                return deleteValid.deleted_count
            except Exception as e:
                raise Exception(f"Error deleting data: {str(e)}")
        else:
            raise Exception("Nothing to delete")