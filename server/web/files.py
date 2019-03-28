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
        try:
            # For Any Authorization
            if True:
                # * for list all files
                files = yield os.listdir(self.fu.uploads_path)
                if len(files):
                    result = files
                    status = True
                else:
                    status = False
                    message = 'No Files Found'
                    code = 4003
            else:
                status = False
                message = 'You are not Authorized'
                code = 4003
        except Exception, e:
            Log.r('EXC', e)
            status = False
            message = 'Logical ERROR'
            code = 5010
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
                    try:
                        # Creating File Path
                        if not (os.path.exists(self.fu.uploads_path)):
                            os.system('mkdir ' + self.fu.uploads_path)
                        f_path = self.fu.uploads_path + f_name
                        fm = open(f_path, 'w')
                        fm.write(f_raw)
                        fm.close()
                        message = 'File Has been saved '
                        result.append(f_name)
                        code = 2000
                    except Exception, e:
                        Log.r('EXC', e)
                        status = False
                        message = 'ERROR when saving File'
                        code = 5040
                else:
                    status = False
                    message = 'Validation ERROR'
                    code = 5030
            else:
                status = False
                message = 'You are not Authorized'
                code = 4003
        except Exception, e:
            Log.r('EXC', e)
            status = False
            message = 'Logical EROOR'
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

