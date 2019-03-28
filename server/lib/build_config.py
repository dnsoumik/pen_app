#!/usr/bin/env python
# -*- coding: utf-8 -*-

# SERVER BUILD LIBS

from __future__ import division

import cyclone
import cyclone.web
import json
import time
import os
import os.path
import json
import base64
import hashlib
import mimetypes

from datetime import datetime as dtime
from twisted.internet import defer
from twisted.application import service, internet
from twisted.internet.defer import inlineCallbacks

# WEB SERVER CONFIGURATION
WEB_SERVER_PORT = 2200
WEB_SERVER_INTERFACE = '127.0.0.1'

CONFIG_FILE = '../Configuration.json'
