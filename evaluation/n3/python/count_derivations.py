import rdflib
g = rdflib.Graph()
case1 = "derivations/case1_derivations.ttl"
g.parse(case1)

count_query = """
SELECT (COUNT(*) as ?Triples) 
WHERE { ?s a <https://bioportal.bioontology.org/ontologies/DMTO.owl#DMTO_0001712> } 
"""

qres = g.query(count_query)
for row in qres:
    print(f"Case 1: {row.Triples}")
