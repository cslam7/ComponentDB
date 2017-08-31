#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


import cherrypy
from cdb.common.service.cdbController import CdbController
from cdb.common.exceptions.invalidRequest import InvalidRequest
from cdb.cdb_web_service.impl.itemControllerImpl import ItemControllerImpl
from cdb.common.utility.encoder import Encoder


class ItemController(CdbController):

    def __init__(self):
        CdbController.__init__(self)
        self.itemControllerImpl = ItemControllerImpl()

    @cherrypy.expose
    @CdbController.execute
    def getItemById(self, itemId):
        if not itemId:
            raise InvalidRequest("Invalid item id provided")
        response = self.itemControllerImpl.getItemById(itemId).getFullJsonRep()
        self.logger.debug('Returning item info for %s: %s' % (itemId, response))
        return response

    @cherrypy.expose
    @CdbController.execute
    def getItemByUniqueAttributes(self, domainId, itemName, itemIdentifier1=None, itemIdentifier2=None, derivedFromItemId=None):
        if not domainId:
            raise InvalidRequest("Invalid domain id provided")
        if not itemName:
            raise InvalidRequest("Invalid itemName provided")

        itemName = Encoder.decode(itemName)

        if itemIdentifier1 is not None:
            itemIdentifier1 = Encoder.decode(itemIdentifier1)

        if itemIdentifier2 is not None:
            itemIdentifier2 = Encoder.decode(itemIdentifier2)

        item = self.itemControllerImpl.getItemByUniqueAttributes(domainId, itemName, itemIdentifier1, itemIdentifier2, derivedFromItemId)
        response = item.getFullJsonRep()

        self.logger.debug('Returning item info for item in domain %s with name %s: %s' % (domainId, itemName, response))
        return response

    def getParentItems(self, itemId):
        if not itemId:
            raise InvalidRequest("Invalid item id provided")
        response = self.listToJson(self.itemControllerImpl.getParentItems(itemId))
        self.logger.debug('Returning item element list for item: %s: %s' % (itemId, response))
        return response

    def getItemElementsForItem(self, itemId):
        if not itemId:
            raise InvalidRequest("Invalid itemId provided")

        response = self.listToJson(self.itemControllerImpl.getItemElementsForItem(itemId))
        self.logger.debug('Returning item element list for item: %s: %s' % (itemId, response))
        return response

    @cherrypy.expose
    @CdbController.execute
    def getItemLogsByQrId(self, qrId):
        if not qrId:
            raise InvalidRequest("Invalid item QrId provided")

        logs = self.itemControllerImpl.getLogEntriesForItemWithQrId(qrId)
        response = self.listToJson(logs)
        self.logger.debug('Returning log entries for item with qrid: %s' % qrId)
        return response

    @cherrypy.expose
    @CdbController.execute
    def getItemLogsById(self, itemId):
        if not itemId:
            raise InvalidRequest("Invalid item id provided")

        logs = self.itemControllerImpl.getLogEntriesForItemWithId(itemId)
        response = self.listToJson(logs)
        self.logger.debug('Returning log entries for item with id: %s' % itemId)
        return response

    @cherrypy.expose
    @CdbController.execute
    def getCatalogItems(self):
        catalogItems = self.itemControllerImpl.getCatalogItems()
        response = self.listToJson(catalogItems)
        return response

    @cherrypy.expose
    @CdbController.execute
    def getItemsDerivedFromItem(self, derivedFromItemId):
        if not derivedFromItemId:
            raise InvalidRequest("Invalid derived from item id provided")

        catalogItems = self.itemControllerImpl.getItemsDerivedFromItemId(derivedFromItemId)
        response = self.listToJson(catalogItems)
        return response

    @cherrypy.expose
    @CdbController.execute
    def getPropertiesForItemByItemId(self, itemId):
        if not itemId:
            raise InvalidRequest("Invalid item id provided")

        properties = self.itemControllerImpl.getPropertiesForItemId(itemId)
        response = self.listToJson(properties)
        return response

    @cherrypy.expose
    @CdbController.execute
    def getDomains(self):
        domains = self.itemControllerImpl.getDomains()
        response = self.listToJson(domains)
        return response

