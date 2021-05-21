#### A directory to store audio resources for module [SPEAKER](../speaker)

Notice that this is a maven resource dir, 
so need to compile first for the application to pick up changes.

The structure of the directory is expected to be following:

```
lang1
    archive-1-1.tar
    archive-1-2.tar
    ...
    archive-1-N.tar
lang2    
    archive-2-1.tar
    ...
...    
langN    
```
where each `langN` is a language tag (e.g. `en`, `de`, `ee`) 
and `archive-n-m.tar` is a tar-archive with `flac\*.flac` files inside. 
Also archive must contain `flac\index.tags.txt` - a map to audio-files (right now `.flac` only).  

See also http://download.shtooka.net/ - a collection of audio-resources in a suitable format. 