package controllers;

import play.mvc.Controller;
import play.mvc.Security.Authenticated;
import play.mvc.Security.Authenticator;

@Authenticated(Authenticator.class)
public class SecureController extends Controller {}
