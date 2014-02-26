---
is_for_config : true
page_no : 8
date : 2014-02-08
category : configuration
title : Provisioning
desc: Details of Playground that is available for download.
categories : 
- configuration
---


# Overview

A Vagrant Box has been created (The vagrant box is a VirtualBox Centos 6.5 VM), that contains the Relate It application
(Search and Indexing); and an instance of Elasticsearch.

This vagrant box is provided as a playground for you to experiment with the Relate It application and that of elasticsearch.
The vagrant box has been pre-created using Vagrant 1.4.3 and Ansible 1.4.5.  The `Vagrantfile` and the playbook for creating the 
vm can be found at: [Relate it playground provisioning](https://github.com/tootedom/related/tree/master/provisioning/ansible-playground). 


----

## Playground Requirements

In order to use the Playground you need two pieces of software

- [Virtual Box 4.3.6+](https://www.virtualbox.org/wiki/Downloads)
- [Vagrant 1.4.3+](http://www.vagrantup.com/downloads.html)


Please down load and install the above.

----

## Add and Download the vagrant playground box

Run the following command to install the box into Vagrant.  This command will download the .box file and install it into Vagrant.

    vagrant box add relateitplayground http://bit.ly/1fFxBrn  

One downloaded you sill be able to run `vagrant box list` and it will show you the 'Vagrant' boxes you have available on your machine:

     > vagrant box list
     relateitplayground       (virtualbox)    

Once the vagrant box has "added", the create a new directory and change into it.  Inside that directory create a file named `Vagrantfile`, 
and put the following in it:

    Vagrant.configure("2") do |config|
      config.vm.define "relateit" do |conf|
        conf.vm.hostname = "relateit"
        conf.vm.box = "relateitplayground"
        conf.vm.network "forwarded_port", guest: 8080, host: 28080
        conf.vm.network "forwarded_port", guest: 9200, host: 29200         
        conf.vm.provider "virtualbox" do |v|
          v.memory = 2048
        end
      end
    end    


Once added run:

    vagrant up

This will start the Virtual Machine which will have the following ports forwarded to the virtual machine:

- 28080 forwarding to 8080 (tomcat http) on the VM
- 29200 forwarding to 9200 (elasticsearch http) on the VM


To log into the virtual machine you can run `vagrant ssh`

The relateit searching and web application is running in the tomcat at `/usr/share/tomcat`.  This can be stopped and started with

    service tomcat stop
    service tomcat start


The elasticsearch instance can be stopped an started with the following:

    service elasticsearch stop
    service elasticsearch start


When the machine is started with vagrant the tomcat and elasticsearch instance will be start when the Virtual Machine starts    

This means for indexing or searching you can execute requests against port 28080 locally, which will be automatically routed
to port 8080 on the VM.  For example

To index a related item execute the following

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

After waiting a second or two, you should be able to execute the following search and obtain a result (the default install
of elasticsearch commits every 1second).

    curl http://localhost:28080/searching/frequentlyrelatedto/1    


----

## How the Playground was created

The source for the provisioning can be found here: 

- [Relate it playground provisioning](https://github.com/tootedom/related/tree/master/provisioning/ansible-playground).

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

This means for indexing or searching you can execute requests against port 28080 locally, which will be automatically routed
to port 8080 on the VM.  For example

To index a related item execute the following

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

After waiting a second or two, you should be able to execute the following search and obtain a result (the default install
of elasticsearch commits every 1second).

    curl http://localhost:28080/searching/frequentlyrelatedto/1    


