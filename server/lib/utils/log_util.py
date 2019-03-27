#!/usr/bin/env python
# -*- coding: utf-8 -*-

from twisted.python import log

# Log Utils class
class Log:

    # For Information py_log
    @staticmethod
    def i(tag, message):
        log.msg('[I] ' + str(tag) + ':', str(message))

    # For error py_log
    @staticmethod
    def e(tag, message):
        log.msg('[E] ' + str(tag) + ':', str(message))

    # For debug py_log
    @staticmethod
    def d(tag, message):
        log.msg('[D] ' + str(tag) + ':', str(message))

    # For warning py_log
    @staticmethod
    def w(tag, message):
        log.msg('[W] ' + str(tag) + ':', str(message))

    # Showing results py_log
    @staticmethod
    def r(tag, message):
       log.msg('[R] ' + str(tag) + ':', str(message))

