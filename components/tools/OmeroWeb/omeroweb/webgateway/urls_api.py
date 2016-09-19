#!/usr/bin/env python
# -*- coding: utf-8 -*-

# Copyright (C) 2016 University of Dundee & Open Microscopy Environment.
# All rights reserved.
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.

# Handles all 'api' urls without including '/webgateway/' in the url.

from django.conf.urls import url, patterns
from omeroweb.webgateway import views
from omeroweb.webgateway.views import LoginView
from django.conf import settings
import re

versions = '|'.join([re.escape(v)
                    for v in settings.API_VERSIONS])

api_versions = url(r'^$', views.api_versions, name='api_versions')

api_base = url(r'^v(?P<api_version>' + versions + ')/$',
               views.api_base,
               name='api_base')
"""
GET various urls listed below
"""

api_token = url(r'^v(?P<api_version>' + versions + ')/token/$',
                views.api_token,
                name='api_token')
"""
GET the CSRF token for this session. Needs to be included
in header with all POST, PUT & DELETE requests
"""

api_servers = url(r'^v(?P<api_version>' + versions + ')/servers/$',
                  views.api_servers,
                  name='api_servers')
"""
GET list of available OMERO servers to login to.
"""

api_login = url(r'^v(?P<api_version>' + versions + ')/login/$',
                LoginView.as_view(),
                name='api_login')
"""
Login to OMERO. POST with 'username', 'password' and 'server' index
"""

api_save = url(r'^v(?P<api_version>' + versions + ')/m/save/$',
               views.SaveView.as_view(),
               name='api_save')
"""
POST to create a new object or PUT to update existing object.
In both cases content body encodes json data.
"""

api_projects = url(r'^v(?P<api_version>' + versions + ')/m/projects/$',
                   views.ProjectsView.as_view(),
                   name='api_projects')
"""
GET all projects, using omero-marshal to generate json
"""

api_project = url(
    r'^v(?P<api_version>' + versions + ')/m/projects/(?P<pid>[0-9]+)/$',
    views.ProjectView.as_view(),
    name='api_project')
"""
Project url to GET or DELETE a single Project
"""

urlpatterns = patterns(
    '',
    api_versions,
    api_base,
    api_token,
    api_servers,
    api_login,
    api_save,
    api_projects,
    api_project,
)
