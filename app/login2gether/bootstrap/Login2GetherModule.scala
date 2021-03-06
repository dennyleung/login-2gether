package login2gether.bootstrap

import com.google.inject.AbstractModule
import login2gether.service._

class Login2GetherModule extends AbstractModule {
  protected def configure: Unit = {
    bind(classOf[InitialData]).asEagerSingleton()
    bind(classOf[SecretService]).to(classOf[DefaultSecretService])
    bind(classOf[AuthenticationService]).to(classOf[DefaultAuthenticationService])
  }
}
