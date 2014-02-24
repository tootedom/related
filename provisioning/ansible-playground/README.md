# Overview

This folder contains a ansible playbook that installs on a Vagrant vm the relate it application and elasticsearch.

## Development standalone Relate it virtual machine.

- Requires Ansible 1.4 or newer
- Requires Virtualbox 4.3.6+
- Requires Vagrant 1.4.3 and CentOS/RHEL 6.x hosts from https://github.com/2creatives/vagrant-centos/releases/tag/v6.5.1
    

## Installing ansible

Details for installing ansible can be found at: http://docs.ansible.com/intro_installation.html
However, for a quick step guide for installation on Mac OS X.

    sudo easy_install pip
    sudo pip install ansible

To upgrade after install run:

    sudo pip install ansible --upgrade

If you get the following error: `configure: error: cannot find sources (src/pycrypto_compat.h)` 
Then it could be one of two things:

**1)** Out of date XCode it's an xcode thing 
**2)** Or you need to install pycyrpto from source (http://superuser.com/questions/259278/python-2-6-1-pycrypto-2-3-pypi-package-broken-pipe-during-build) when compiling pycrypto

If (2) try to do the following: 

    download pycrpto source (https://www.dlitz.net/software/pycrypto/):
    untar and cd into it
    run: ./configure
    sudo python ./setup.py build
    sudo python ./setup.py install    

## Install Virtual box

Install Virtual box from https://www.virtualbox.org/wiki/Downloads
    
## Vagrant

Install vagrant from http://www.vagrantup.com/downloads.html

Then add the following box:

    vagrant box add centos65-x86_64-20131205 https://github.com/2creatives/vagrant-centos/releases/download/v6.5.1/centos65-x86_64-20131205.box


## Building/Provisioning the Vagrant virtual machine

The Vagrant/Ansible provisioning in this folder creates a vm that has:

- Elasticsearch 1.0.0
- Relateit Indexing and Searching application on tomcat 7.0.52, on Oracle jdk1.7.0_51


Provisioning the vm is a matter of running `vagrant up`

    vagrant up


## Talking to the Indexing and Search application on the Virtual Machine

The virtual machine, when started by Vagrant, has the following ports forwarded to the virtual machine:

- 28080 forwarding to 8080 (tomcat http) on the VM
- 29200 forwarding to 9200 (elasticsearch http) on the VM

- Indexing

    curl -H"Content-Type:text/json" -XPOST -v http://localhost:28080/indexing/index -d '
    {
       "channel":"de",
       "site":"amazon",
       "items":[
          {
             "id":"1",
             "type":"map"
	      },
          {
             "id":"2",
             "type":"compass"
          },
          {
             "id":"3",
             "type":"torch"
          },
          {
             "id":"4",
             "type":"torch",
             "channel":"uk"
          }
        ]
    }'

- Searching

    curl http://localhost:28080/searching/frequentlyrelatedto/1    


