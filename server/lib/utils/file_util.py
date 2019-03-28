#!/usr/bin/env python
# -*- coding: utf-8 -*-

from build_config import *

class FileUtil():

    try:
        config_file = open(CONFIG_FILE)
        config = json.loads(config_file.read())
        config_file.close()
    except Exception, e:
        raise ValueError(e)

    server_url = config['url']
    uploads_path = config['uploads_path']

