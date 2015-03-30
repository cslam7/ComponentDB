#!/usr/bin/env python

from cdb.common.exceptions.cdbException import CdbException
from cdb.common.db.api.cdbDbApi import CdbDbApi
from cdb.common.db.impl.userInfoHandler import UserInfoHandler
from cdb.common.db.impl.userGroupHandler import UserGroupHandler

class UserDbApi(CdbDbApi):

    def __init__(self):
        CdbDbApi.__init__(self)
        self.userInfoHandler = UserInfoHandler()
        self.userGroupHandler = UserGroupHandler()

    @CdbDbApi.executeQuery
    def getUserGroups(self, **kwargs):
        session = kwargs['session']
        dbUserGroups = self.userGroupHandler.getUserGroups(session)
        return self.toCdbObjectList(dbUserGroups)

    @CdbDbApi.executeQuery
    def getUsers(self, **kwargs):
        session = kwargs['session']
        dbUsers = self.userInfoHandler.getUserInfos(session)
        return self.toCdbObjectList(dbUsers)

    @CdbDbApi.executeQuery
    def getUserById(self, id, **kwargs):
        session = kwargs['session']
        dbUserInfo = self.userInfoHandler.getUserInfoById(session, id)
        return dbUserInfo.getCdbObject()

    @CdbDbApi.executeQuery
    def getUserByUsername(self, username, **kwargs):
        session = kwargs['session']
        dbUserInfo = self.userInfoHandler.getUserInfoByUsername(session, username)
        return dbUserInfo.getCdbObject()

    @CdbDbApi.executeQuery
    def getUserWithPasswordByUsername(self, username, **kwargs):
        session = kwargs['session']
        dbUserInfo = self.userInfoHandler.getUserInfoWithPasswordByUsername(session, username)
        return dbUserInfo.getCdbObject()

#######################################################################
# Testing.
if __name__ == '__main__':
    api = UserDbApi()
    user = api.getUserByUsername('sveseli')
    print user
    user = api.getUserWithPasswordByUsername('sveseli')
    print user
    users = api.getUsers()
    for user in users:
        print user.getDictRep()
        print user.__dict__

    print 
    print 'User Groups'
    print '***********'
    userGroups = api.getUserGroups()
    for userGroup in userGroups:
        print userGroup.getDictRep()

