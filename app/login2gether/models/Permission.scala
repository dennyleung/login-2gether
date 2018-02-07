package login2gether.models

case class Permission(requestedByUser: User, grantedByUserOption: Option[User] = None) {

  def isGranted : Boolean = {
    grantedByUserOption.exists { grantedByUser => grantedByUser.mateIdOption.contains(requestedByUser.id) }
  }
}
