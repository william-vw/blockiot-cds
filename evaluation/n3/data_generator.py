from rdflib import Graph, URIRef, Literal, BNode, Namespace
from rdflib.namespace import RDF, FOAF, XSD
import csv

g = Graph()
nm = g.namespace_manager
g.bind("dmto", "https://bioportal.bioontology.org/ontologies/DMTO.owl#")
g.bind("ddo", "http://purl.obolibrary.org/obo/DDO.owl#")

prefix_dmto = "dmto"
ns_dmto = Namespace("https://bioportal.bioontology.org/ontologies/DMTO.owl#")
nm.bind(prefix_dmto, ns_dmto)

prefix_ddo = "ddo"
ns_ddo = Namespace("http://purl.obolibrary.org/obo/DDO.owl#")
nm.bind(prefix_ddo, ns_ddo)

prefix_cg = "cg"
ns_cg = Namespace("http://niche.cs.dal.ca/codegen.owl#")
nm.bind(prefix_cg, ns_cg)

case1_filename = "data/case1.csv"

with open(case1_filename, 'r') as csvfile:
    datareader = csv.DictReader(csvfile)
    for row in datareader:
        patient_identifier = row["HADM_ID"]
        height = row["HEIGHT"]
        weight = row["WEIGHT"]
        bmi = row["BMI"]

        g.add((ns_cg["exam"+patient_identifier], RDF.type, ns_ddo.DDO_0000230))
        g.add((ns_cg["exam"+patient_identifier], ns_ddo.DDO_0000134, Literal(bmi, datatype=XSD.integer)))

        g.add((ns_cg["patient"+patient_identifier], RDF.type, ns_dmto.DMTO_0000021))
        diagnosis_bnode = BNode()
        g.add((ns_cg["patient"+patient_identifier+"_profile"], ns_ddo.DDO_0000114, diagnosis_bnode))
        diabetes_status_bnode = BNode()
        g.add((diagnosis_bnode, ns_dmto.DMTO_0001673, diabetes_status_bnode))
        g.add((diabetes_status_bnode, RDF.type, ns_ddo.DDO_0003905))
        g.add((ns_cg["patient"+patient_identifier+"_profile"], ns_dmto.DMTO_0001671, ns_cg["patient"+patient_identifier+"_plan"]))


g.serialize(destination="output/case1.ttl", format="ttl", encoding="utf-8")
