# lein-web-template
A [leiningen](https://github.com/technomancy/leiningen) plugin to create a [ring](https://github.com/ring-clojure/ring)-[compojure](https://github.com/weavejester/compojure) web project. It packages [Moustache](http://mustache.github.com/mustache.5.html) (using [stencil](https://github.com/davidsantiago/stencil))  and [Hiccup](https://github.com/weavejester/hiccup) rendering, you can add others if you need. It only works with leiningen 2.

## Installing
Just add `lein-web-template` in your `:plugins` section of your `:user` profile in `~/.lein/profiles.clj`:
```clojure
{:user {:plugins [[org.clojars.kiran/lein-web-template "0.2.1"]]}}
```
on next run of `lein` plugin will be installed

## Usage

To create a new project simply execute following command

    $ lein new web-template <project_name>

Replace <project_name> with your project name
e.g.

    $ lein new web-template nuggets

You will get following tree

    $ cd nuggets
    $ tree
    .
    |___ project.clj
    |___ resources
    | |___ static
    | | |___ css
    | | | |___ screen.css
    | |___ templates
    | | |___ stencil.html
    |___ src
    | |___ nuggets
    | | |___ core.clj

To test the server execute run command

    $ lein run
    Compiling nuggets.core
    2013-01-08 13:13:07.446:INFO:oejs.Server:jetty-7.6.1.v20120215
    Started server, Browse http://localhost:8080/
    2013-01-08 13:13:07.493:INFO:oejs.AbstractConnector:Started SelectChannelConnector@0.0.0.0:8080

As you can see server will be started on `8080` port.
