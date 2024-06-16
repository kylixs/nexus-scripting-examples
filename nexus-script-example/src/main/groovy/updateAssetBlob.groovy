import groovy.sql.Sql
import org.apache.ibatis.session.Configuration
import org.apache.ibatis.session.SqlSession
import org.sonatype.nexus.blobstore.api.BlobRef
import org.sonatype.nexus.datastore.internal.DataStoreManagerImpl
import org.sonatype.nexus.content.maven.store.Maven2AssetBlobDAO
import org.sonatype.nexus.common.entity.Continuation
//import org.sonatype.nexus.repository.storage.AssetBlob


DataStoreManagerImpl dataStoreManager = container.lookup(org.sonatype.nexus.datastore.internal.DataStoreManagerImpl)
def session = dataStoreManager.openSession("nexus")

try {

    // Use Groovy SQL
    Sql sql = new Sql(session.session.getConnection())

    def objects = sql.rows("SELECT ma.asset_id, ma.path, ma.component_id, ma.asset_blob_id, mab.created_by, mab.created_by_ip, mab.blob_created,ma.last_updated \n" +
            "FROM MAVEN2_ASSET  ma  \n" +
            "inner join maven2_asset_blob mab \n" +
            "on ma.asset_blob_id = mab.asset_blob_id ")
    log.info("results: " + objects)
    return objects

//    Maven2AssetBlobDAO assetBlobDao = session.access(org.sonatype.nexus.content.maven.store.Maven2AssetBlobDAO)
//
//    Continuation blobs = assetBlobDao.browseAssetBlobs(1000, null)
//    def blobRefs = blobs.stream().map { blob -> assetBlobDao.setCreatedBy(blob.blobRef, "gdw"); blob.blobRef }.toList()
//    session.commit()
//
//    log.info("update blobs: " + blobRefs)
//    return blobRefs

//    def blobRef = BlobRef.parse("default@5f83ec08-3455-4286-bb54-0040351813b0")
//    assetBlob = assetBlobDao.readAssetBlob(blobRef)
//    log.info("read assetBlob: " + assetBlob.get())
//    // update
//    assetBlobDao.setCreatedBy(blobRef, "gdw")
//    // check
//    assetBlob = assetBlobDao.readAssetBlob(blobRef)
//    log.info("update assetBlob: " + assetBlob.get())
//    session.commit()
//    return assetBlob.get()

} finally {
    session.close()
}


