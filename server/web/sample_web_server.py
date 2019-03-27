#!/usr/bin/env python
# -*- coding: utf-8 -*-

from common import *

class SampleWebServerApplication(cyclone.web.Application):
    def __init__(self, app):

        stats = Stats()

        handlers = [
            (r'/', MainHandler, dict(stats=stats)),
            (r'/sample/api/rt_socket', RealtimeWebSocketHandler, dict(stats=stats)),
            (r'/sample/api/files', FileUploadHandler),
            (r'/(.*)', cyclone.web.StaticFileHandler, {'path': 'static'}),
        ]
        cyclone.web.Application.__init__(self, handlers)

class MainHandler(BaseHandler):
    def initialize(self, stats):
        self.stats = stats

    @cyclone.web.authenticated
    def get(self):
        self.stats.newVisit()
        self.render('index.html', messages=ChatSocketHandler.cache)

application = service.Application('SAMPLE WEB SERVER')
WEB_SERVER_APP = SampleWebServerApplication(application)
server = internet.TCPServer(WEB_SERVER_PORT, WEB_SERVER_APP, interface=WEB_SERVER_INTERFACE)
server.setServiceParent(application)

