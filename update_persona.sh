sed -i '/<\/f:facet>/a \
            <f:facet name="relaciones">\
                <p:outputPanel rendered="#{personaModel.estadoModificar}">\
                    <p:tabView style="margin-top:1rem">\
                        <p:tab title="Documentos">\
                            <p:dataTable value="#{personaModel.documentosAsignados}" var="doc" emptyMessage="Sin documentos." size="small">\
                                <p:column headerText="Tipo"><h:outputText value="#{doc.idTipoDocumento.nombre}"/></p:column>\
                                <p:column headerText="Valor"><h:outputText value="#{doc.valor}"/></p:column>\
                                <p:column headerText="Ruta"><h:outputText value="#{doc.rutaFisica}"/></p:column>\
                                <p:column headerText="Acción">\
                                    <p:commandButton icon="pi pi-trash" styleClass="ui-button-danger" action="#{personaModel.quitarDocumento(doc)}" process="@this" update="@form">\
                                        <p:confirm header="Confirmar" message="¿Remover documento?" icon="pi pi-exclamation-triangle"/>\
                                    </p:commandButton>\
                                </p:column>\
                            </p:dataTable>\
                            <p:panelGrid columns="2" style="width:100%; margin-top:1rem">\
                                <p:outputLabel value="Tipo:" for="idTipoDocNew"/>\
                                <p:selectOneMenu id="idTipoDocNew" value="#{personaModel.nuevoDocumento.idTipoDocumento}" converter="#{entityConverter}">\
                                    <f:selectItem itemLabel="Seleccione..." itemValue="#{null}" noSelectionOption="true"/>\
                                    <f:selectItems value="#{tipoDocumentoModel.registros}" var="td" itemLabel="#{td.nombre}" itemValue="#{td}"/>\
                                </p:selectOneMenu>\
                                <p:outputLabel value="Valor:" for="valorDocNew"/>\
                                <p:inputText id="valorDocNew" value="#{personaModel.nuevoDocumento.valor}"/>\
                                <p:outputLabel value="Ruta Física:" for="rutaDocNew"/>\
                                <p:inputText id="rutaDocNew" value="#{personaModel.nuevoDocumento.rutaFisica}"/>\
                                <h:outputText value=""/>\
                                <p:commandButton value="Agregar Documento" icon="pi pi-plus" styleClass="ui-button-success" action="#{personaModel.agregarDocumento()}" process="@this idTipoDocNew valorDocNew rutaDocNew" update="@form"/>\
                            </p:panelGrid>\
                        </p:tab>\
                        <p:tab title="Medios de Contacto">\
                            <p:dataTable value="#{personaModel.mediosContactoAsignados}" var="mc" emptyMessage="Sin medios de contacto." size="small">\
                                <p:column headerText="Tipo"><h:outputText value="#{mc.idTipoMedioContacto.nombre}"/></p:column>\
                                <p:column headerText="Valor"><h:outputText value="#{mc.valor}"/></p:column>\
                                <p:column headerText="Acción">\
                                    <p:commandButton icon="pi pi-trash" styleClass="ui-button-danger" action="#{personaModel.quitarMedioContacto(mc)}" process="@this" update="@form">\
                                        <p:confirm header="Confirmar" message="¿Remover medio de contacto?" icon="pi pi-exclamation-triangle"/>\
                                    </p:commandButton>\
                                </p:column>\
                            </p:dataTable>\
                            <p:panelGrid columns="2" style="width:100%; margin-top:1rem">\
                                <p:outputLabel value="Tipo:" for="idTipoMcNew"/>\
                                <p:selectOneMenu id="idTipoMcNew" value="#{personaModel.nuevoMedioContacto.idTipoMedioContacto}" converter="#{entityConverter}">\
                                    <f:selectItem itemLabel="Seleccione..." itemValue="#{null}" noSelectionOption="true"/>\
                                    <f:selectItems value="#{tipoMedioContactoModel.registros}" var="tmc" itemLabel="#{tmc.nombre}" itemValue="#{tmc}"/>\
                                </p:selectOneMenu>\
                                <p:outputLabel value="Valor:" for="valorMcNew"/>\
                                <p:inputText id="valorMcNew" value="#{personaModel.nuevoMedioContacto.valor}"/>\
                                <h:outputText value=""/>\
                                <p:commandButton value="Agregar Medio" icon="pi pi-plus" styleClass="ui-button-success" action="#{personaModel.agregarMedioContacto()}" process="@this idTipoMcNew valorMcNew" update="@form"/>\
                            </p:panelGrid>\
                        </p:tab>\
                        <p:tab title="Roles">\
                            <p:dataTable value="#{personaModel.rolesAsignados}" var="rol" emptyMessage="Sin roles." size="small">\
                                <p:column headerText="Rol"><h:outputText value="#{rol.idRol.nombre}"/></p:column>\
                                <p:column headerText="Clínica"><h:outputText value="#{rol.idClinica.nombre}"/></p:column>\
                                <p:column headerText="Acción">\
                                    <p:commandButton icon="pi pi-trash" styleClass="ui-button-danger" action="#{personaModel.quitarRol(rol)}" process="@this" update="@form">\
                                        <p:confirm header="Confirmar" message="¿Remover rol?" icon="pi pi-exclamation-triangle"/>\
                                    </p:commandButton>\
                                </p:column>\
                            </p:dataTable>\
                            <p:panelGrid columns="2" style="width:100%; margin-top:1rem">\
                                <p:outputLabel value="Rol:" for="idRolNew"/>\
                                <p:selectOneMenu id="idRolNew" value="#{personaModel.nuevoRol.idRol}" converter="#{entityConverter}">\
                                    <f:selectItem itemLabel="Seleccione..." itemValue="#{null}" noSelectionOption="true"/>\
                                    <f:selectItems value="#{rolModel.registros}" var="r" itemLabel="#{r.nombre}" itemValue="#{r}"/>\
                                </p:selectOneMenu>\
                                <p:outputLabel value="Clínica:" for="idClinicaNew"/>\
                                <p:selectOneMenu id="idClinicaNew" value="#{personaModel.nuevoRol.idClinica}" converter="#{entityConverter}">\
                                    <f:selectItem itemLabel="Ninguna" itemValue="#{null}" noSelectionOption="true"/>\
                                    <f:selectItems value="#{clinicaModel.registros}" var="c" itemLabel="#{c.nombre}" itemValue="#{c}"/>\
                                </p:selectOneMenu>\
                                <h:outputText value=""/>\
                                <p:commandButton value="Agregar Rol" icon="pi pi-plus" styleClass="ui-button-success" action="#{personaModel.agregarRol()}" process="@this idRolNew idClinicaNew" update="@form"/>\
                            </p:panelGrid>\
                        </p:tab>\
                    </p:tabView>\
                </p:outputPanel>\
            </f:facet>' src/main/webapp/paginas/persona/persona.xhtml
bash update_persona.sh
