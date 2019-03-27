#!/usr/bin/env python
# -*- coding: utf-8 -*-

'''
    Real Time WebSocket Handler
'''

from utils import *

WEB_SERVER_RT_WS_CONNECTIONS = []

class RealtimeWebSocketHandler(cyclone.web.RequestHandler):

    def __init__(self, application, request, **kwargs):
        cyclone.web.RequestHandler.__init__(self, application, request,
                                            **kwargs)
        self.application = application
        self.request = request
        self.transport = request.connection.transport
        self.ws_protocol = WebSocketProtocol(self)
        self.notifyFinish().addCallback(self.connectionLost)

    def initialize(self, stats):
        self.stats = stats

    @defer.inlineCallbacks
    def _execute(self, transforms, *args, **kwargs):
        self.key = None
        try:
            # TODO: for varification
            # aid = str(self.get_arguments('application_id')[0])
            if True:
                self.key = yield self.request.headers['Sec-Websocket-Key']
            else:
                self.closeConnection(401)
                return
        except:
            self.closeConnection(401)
            return
        self._transforms = transforms or list()
        self.request.connection.setRawMode()
        self.request.connection.rawDataReceived = \
            self.ws_protocol.rawDataReceived
        self.ws_protocol.acceptConnection()

    @defer.inlineCallbacks
    def connectionMade(self, *args, **kwargs):
        try:
            self.key = yield self.key
        except:
            Log.i('WS-SOC', 'CODE 3, Format Error!')
        return

    @defer.inlineCallbacks
    def messageReceived(self, jmsg=str):
        self.key = yield self.key
        code = 2000
        message = jmsg
        status = True
        result = []
        Log.i('WS-SOC', 'MSG: ' + jmsg)
        resp =  {
                    'code': code,
                    'status': status,
                    'message': message,
                    'result': result
                }
        # For JSON MSG
        self.sendMessage(resp)
        # For Echo Message
        # self.sendMessage(jmsg)

    def sendMessage(self, data):
        self.ws_protocol.sendMessage(data)

    def _rawDataReceived(self, data):
        self.ws_protocol.handleRawData(data)


    def forbidConnection(self, message):
        self.transport.write(
            "HTTP/1.1 403 Forbidden\r\nContent-Length: %s\r\n\r\n%s" %
            (str(len(message)), message))
        return self.transport.loseConnection()

    def closeConnection(self, ecode):
        self.transport.write(
            "HTTP/1.1 %s Forbidden\r\nContent-Length: 5\r\n\r\n" %
            (str(ecode)))
        return self.transport.loseConnection()

    #@defer.inlineCallbacks
    def connectionLost(self, reason):
        Log.d('WS-LOST-CONN', self.key)
        return

