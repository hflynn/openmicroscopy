#!/usr/bin/env python

"""
   Minimal script to test the {g,s}et{In,Out}put functionality
   of omero.client

   Copyright 2008 Glencoe Software, Inc. All rights reserved.
   Use is subject to license terms supplied in LICENSE.txt

"""

import omero, omero.cli

client = omero.script("test_harness")
pixelsID = client.getInput("pixelsID")
client.setOutput("newPixelsID",-1)


