**Table of Contents**  *generated with [DocToc](http://doctoc.herokuapp.com/)*

- [Details](#details)
- [](#)

## Details

Currently in Developement/Anaylsis Phase


Provides a 2 Centos (6.5)  Virtual Machines (Virtual Box).
1 machine will have that of ElasticSearch.
1 machine will have that of the Searching and Indexing Webapp running tomcat.

The machines are provisioned using Vagrant and Ansible

The Centox Vagrant boxes are from : 

https://github.com/2creatives/vagrant-centos/releases/tag/v6.5.1

----

Installing Ansible:

- http://docs.ansible.com/intro_installation.html

sudo easy_install pip
#sudo pip install paramiko PyYAML jinja2 httplib2
sudo pip install ansible
# upgrading:
sudo pip install ansible --upgrade

# if you get: configure: error: cannot find sources (src/pycrypto_compat.h) 
# Then it's an xcode thing (http://superuser.com/questions/259278/python-2-6-1-pycrypto-2-3-pypi-package-broken-pipe-during-build) when compiling pycrypto
# do: 

download pycrpto:
untar and cd into it
run: ./configure
sudo python ./setup.py build
sudo python ./setup.py install
