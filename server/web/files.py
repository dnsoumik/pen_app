#!/usr/bin/env python
# -*- coding: utf-8 -*-

from utils import *

class FileUploadHandler(cyclone.web.RequestHandler):

    SUPPORTED_METHODS = ('GET', 'POST')
    fu = FileUtil()

    @defer.inlineCallbacks
    def get(self):
        status = True
        code = 2000
        message = ''
        result = []
        resp =  {
                    'status': status,
                    'code': code,
                    'message': message,
                    'result': result
                }
        self.write(resp)
        return

    @defer.inlineCallbacks
    def post(self):
        status = False
        message = ''
        result = []
        code = 0
        try:
            # For Any Authorization
            if True:
                try:
                    f_type = yield mimetypes.guess_extension(
                                    self.request.headers['Content-Type'],
                                    strict=True
                                )
                    if f_type == None:
                        message = 'Invalid File Type'
                        status = False
                        code = 4010
                        self.write(
                                    {
                                        'status': status,
                                        'code': code,
                                        'message': message,
                                        'result': result
                                    }
                                )
                        self.finish()
                        return
                    else:
                        f_name = str(timeNow()) + f_type
                        f_raw = self.request.body
                    status = True
                except Exception, e:
                    Log.r('EXC', e)
                    status = False
                    message = 'Invalid Syntax'
                    code = 4006
                    self.write(
                                {
                                    'status': status,
                                    'code': code,
                                    'message': message,
                                    'result': result
                                }
                              )
                    self.finish()
                    return
                if status:
                    # Creating File Path
                    print 'test'
                else:
                    status = False
                    message = 'Validation Error'
                    code = 5030
            else:
                status = False
                message = 'You are not Authorized'
                code = 4003
        except Exception, e:
            Log.r('EXC', e)
            status = False
            message = 'Logical Error'
            code = 5010
        self.write(
                        {
                            'status': status,
                            'message': message,
                            'code': code,
                            'result': result
                        }
                )
        self.finish()
        return

