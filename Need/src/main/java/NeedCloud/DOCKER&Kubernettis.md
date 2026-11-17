1. DOCKER
   Images (Build, Pull, Push)
   Command
   Description
   docker build -t my-image:tag .
   Build image from Dockerfile
   docker images
   List all local images
   docker pull image:tag
   Download image from registry
   docker push image:tag
   Upload image to registry
   docker tag old:tag new:tag
   Rename/retag an image
   docker rmi image:tag
   Delete an image

Containers (Run, Manage, Debug)
Command
Description
docker run -d -p 8080:8080 --name app image:tag
Run container (detached + port mapped)
docker ps
List running containers
docker ps -a
List all containers (including stopped)
docker stop <id>
Stop a container
docker start <id>
Start a stopped container
docker restart <id>
Restart a container
docker rm <id>
Remove a container
docker logs <id>
View logs
docker logs -f <id>
Stream logs in real time
docker exec -it <id> /bin/bash
Open shell inside container
docker inspect <id>
View detailed container info

Cleanup
Command
Description
docker system prune
Remove all unused data
docker volume prune
Remove unused volumes
docker network prune
Remove unused networks

Docker Compose
Command
Description
docker-compose up -d
Start all services (background)
docker-compose down
Stop and remove all services
docker-compose logs -f
Stream logs for all services
docker-compose ps
List running services
docker-compose build
Rebuild all services


2. KUBERNETES (kubectl)
   Cluster & Context
   Command
   Description
   kubectl cluster-info
   Show cluster details
   kubectl get nodes
   List all nodes
   kubectl config get-contexts
   List available contexts
   kubectl config use-context <name>
   Switch to a context

Pods (View, Debug, Access)
Command
Description
kubectl get pods
List pods (default namespace)
kubectl get pods -n <ns>
List pods in a namespace
kubectl get pods -A
List pods across all namespaces
kubectl describe pod <name>
Detailed pod info + events
kubectl logs <pod>
View pod logs
kubectl logs -f <pod>
Stream pod logs
kubectl logs <pod> -c <container>
Logs for a specific container
kubectl exec -it <pod> -- /bin/bash
Shell into a pod
kubectl delete pod <name>
Delete a pod
kubectl top pods
Show CPU/memory usage

Deployments (Scale, Update, Rollback)
Command
Description
kubectl get deployments
List deployments
kubectl describe deployment <name>
Describe a deployment
kubectl scale deployment <name> --replicas=3
Scale up/down
kubectl rollout status deployment <name>
Check rollout progress
kubectl rollout restart deployment <name>
Restart all pods
kubectl rollout undo deployment <name>
Rollback to previous version
kubectl set image deployment/<name> container=image:tag
Update container image

Services & Networking
Command
Description
kubectl get services
List services
kubectl get ingress
List ingress rules
kubectl port-forward pod/<name> 8080:8080
Forward port to a pod
kubectl port-forward svc/<name> 8080:80
Forward port to a service

Apply & Manage Resources (YAML)
Command
Description
kubectl apply -f file.yaml
Create or update from YAML
kubectl delete -f file.yaml
Delete resources from YAML
kubectl get all -n <ns>
List everything in a namespace
kubectl get configmaps
List ConfigMaps
kubectl get secrets
List Secrets
kubectl get pvc
List Persistent Volume Claims

Debugging & Troubleshooting
Command
Description
kubectl describe pod <name>
Check events and error details
kubectl get events --sort-by=.metadata.creationTimestamp
View recent cluster events
kubectl run debug --image=busybox -it --rm -- /bin/sh
Launch temp debug pod


Quick Comparison: Docker vs Kubernetes
Action
Docker
Kubernetes
Run an app
docker run
kubectl apply -f
View logs
docker logs
kubectl logs
Shell access
docker exec -it
kubectl exec -it
Stop
docker stop
kubectl delete pod
Scale
docker-compose scale
kubectl scale deployment
Port forward
-p 8080:8080 (at run)
kubectl port-forward



