#[MultiExperiment Viewer](http://mev.tm4.org) [![Build Status](https://travis-ci.org/dfci-cccb/mev.svg?branch=master)](https://travis-ci.org/dfci-cccb/mev)

===

To run locally install [docker](https://docs.docker.com/) and [minikube](https://kubernetes.io/docs/getting-started-guides/minikube/#installation) then follow minikube instructions to start up a cluster. Configure docker to use minikube and launch our MeV image
```
eval $(minikube docker-env)
docker run -p8080:8080 -d cccb/mev-web:baylie-2017-02-10
echo http://$(minikube ip):8080
```
The last line of output should be the URL of the application
