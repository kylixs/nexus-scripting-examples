// simple example showing simple method and equivalent method with all default parameters expanded.

repository.createMavenHosted('private')

repository.createMavenHosted('private-again', BlobStoreManager.DEFAULT_BLOBSTORE_NAME, true, VersionPolicy.RELEASE,
        WritePolicy.ALLOW_ONCE, LayoutPolicy.STRICT)
