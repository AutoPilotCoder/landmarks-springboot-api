# Landmarks Springboot Api

Sample project with java springboot and postgresql/postgis (seperated by branches)

# Sample Implementations

## Mapper

Category Entity will have a mapper for basic return data to the CategoryModelAssembler then to the controller

## Assembler

Assemblers for each repository data is implemented to showcase hypermedia on response to follow proper RESTful API
Standards.

## Service

Category Entity will have a service to better decouple the controller from being bloated and handling logic so the
controller can focus on routing requests only.

### Full project implementation of Mapper + Assembler + Service is unnecessary.

Assembler and Service is suitable for most non-complex projects.
